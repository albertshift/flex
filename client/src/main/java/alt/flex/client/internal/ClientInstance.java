package alt.flex.client.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import javax.net.ssl.SSLException;

import alt.flex.client.FlexClient;
import alt.flex.client.FlexClientBuilder;
import alt.flex.client.api.FlexStore;
import alt.flex.client.api.operation.BatchOperation;
import alt.flex.client.internal.io.ClientConnectionInstance;
import alt.flex.client.internal.io.DefaultClientInitializer;
import alt.flex.client.internal.nonstop.TimeoutEntry;
import alt.flex.client.internal.op.BatchOp;
import alt.flex.client.internal.op.InitOp;
import alt.flex.client.support.FlexConfigurationException;
import alt.flex.client.support.FlexConnectionException;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseType;
import alt.flex.protocol.FlexProtocol.ServerParams;
import alt.flex.protocol.FlexProtocol.StoreInformation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * 
 * @author Albert Shift
 *
 */

public class ClientInstance extends FlexClient {

	private static final int INIT_ALIVE_INTERVAL = -1;
	private static final long INIT_HEART_BREATH_EXPIRATION = -1;

	private final AtomicBoolean started = new AtomicBoolean();

	private final UUID clientId = UUID.randomUUID();

	private final ConcurrentHashMap<String, StoreInstance> storeMap = new ConcurrentHashMap<String, StoreInstance>();

	private final FlexClientBuilder settings;
	private final EventLoopGroup group;
	private final Bootstrap bootstrap;
	
	private final AtomicReferenceFieldUpdater<ClientInstance, ClientConnectionInstance> CONNECTION_UPDATER =
			AtomicReferenceFieldUpdater.newUpdater(ClientInstance.class, ClientConnectionInstance.class, "connection");
	
	private volatile ClientConnectionInstance connection = null;
	
	private final AtomicIntegerFieldUpdater<ClientInstance> ALIVE_INTERVAL_UPDATER = 
			AtomicIntegerFieldUpdater.newUpdater(ClientInstance.class, "aliveIntervalMls");
	
	private volatile int aliveIntervalMls = INIT_ALIVE_INTERVAL;
	private final AtomicLong heartBreathExpirationMls = new AtomicLong(INIT_HEART_BREATH_EXPIRATION);

	private final SynchronizationManager synchronizationManager;
	
	private final Executor workerExecutor;
	
	private final AtomicBoolean initInProgress = new AtomicBoolean(false);
	
	public ClientInstance(FlexClientBuilder settings) {
		this.settings = settings;
		
		this.synchronizationManager = new SynchronizationManager(settings);

		this.workerExecutor = settings.getWorkerExecutor() != null ? settings.getWorkerExecutor() : Executors.newCachedThreadPool();

		this.group = new NioEventLoopGroup(settings.getNioThreads());
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(group).channel(NioSocketChannel.class)
				.handler(new DefaultClientInitializer(this, configureSsl(), settings));
	}

	@Override
	public FlexStore getStore(String name) {
		return storeMap.get(name);
	}

	@Override
	public Set<String> getStoreNames() {
		return new HashSet<String>(storeMap.keySet());
	}
	
	@Override
	public BatchOperation batch() {
		return new BatchOp(this);
	}

	@Override
	public void start() {
		
		checkOnlyStartedOnce();
		
		synchronizationManager.start(workerExecutor);
		
		workerExecutor.execute(new ConnectTask(this, true));

	}
	
	@Override
	public boolean isActive() {
		return connection != null && !initInProgress.get();
	}

	protected void connect() {
		
		disconnect();
		
		ClientConnectionInstance conn = new ClientConnectionInstance(bootstrap, settings);
		CONNECTION_UPDATER.set(this, conn);
		
		init();
		
	}

	protected void disconnect() {

		ClientConnectionInstance tmp = CONNECTION_UPDATER.getAndSet(this, null);
		if (tmp != null) {
			tmp.close();
		}
	}

	@Override
	public void shutdown() {
		
		checkStarted();
		
		disconnect();
		group.shutdownGracefully();
		
		if (workerExecutor instanceof ExecutorService) {
			((ExecutorService) workerExecutor).shutdown();
		}
	}

	public void onClosedConnection() {
		
		disconnect();
		
		if (settings.useReconnect() && !initInProgress.get()) {

			workerExecutor.execute(new ConnectTask(this, false));
			
		}
	}

	private void addStores(List<StoreInformation> stores) {
		
		for (StoreInformation store : stores) {
			storeMap.putIfAbsent(store.getName(), new StoreInstance(this, store));
		}
		
	}
	
	private SslContext configureSsl() {
		if (settings.useSsl()) {
			try {
				return SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
			} catch (SSLException e) {
				throw new FlexConfigurationException("fail to initialize ssl context", e);
			}
		}
		return null;
	}

	private boolean isHealthyConnection() {
		long current = System.currentTimeMillis();
		long expire = heartBreathExpirationMls.get();
		if (expire == INIT_HEART_BREATH_EXPIRATION) {
			return true;
		}
		return current < expire;
	}

	protected void init() {

		if (!initInProgress.compareAndSet(false, true)) {
			return;
		}
		
		try {

			doInit();

		}
		finally {
			this.initInProgress.set(false);
		}
		
	}

	private void doInit() {
		
		ALIVE_INTERVAL_UPDATER.set(this, INIT_ALIVE_INTERVAL);
		this.heartBreathExpirationMls.set(INIT_HEART_BREATH_EXPIRATION);
		
		InitOp initOp = new InitOp(this, clientId, synchronizationManager.getCurrentSeq(), storeMap.isEmpty());
		
		Response response = initOp.sync();

		if (response.getResponseType() != ResponseType.INITED || !response.hasServerParams()) {
			throw new FlexConnectionException("fail to init connection, invalid response " + response);
		}
		
		ServerParams serverParams = response.getServerParams();
		ALIVE_INTERVAL_UPDATER.set(this, serverParams.getHeartBreathIntervalMls() * 2);

		processHeartBreath();
		
		if (response.getStoreCount() > 0) {
			addStores(response.getStoreList());
		}
		
	}
	
	public void processHeartBreath() {
		if (aliveIntervalMls != INIT_ALIVE_INTERVAL) {
			heartBreathExpirationMls.set(System.currentTimeMillis() + aliveIntervalMls);
		}
		else {
			heartBreathExpirationMls.set(System.currentTimeMillis() + ClientConstants.DEFAULT_ALIVE_INTERVAL_MLS);
		}
	}
	
	public boolean send(Request request, boolean checkExpiration) {
		
		if (checkExpiration) {
			
			long currentSeq = synchronizationManager.getCurrentSeq();
			
			if (isExpired(request, currentSeq)) {
				return false;
			}
		
		}
		
		ClientConnectionInstance conn = this.connection;
		
		if (conn == null) {
			return false;
		}
		
		Channel channel = conn.getChannel();
		
		if (isHealthyConnection() && channel.isActive()) {
			
			channel.writeAndFlush(request);
			
			return true;
			
		} else {
			
			disconnect();
			
			return false;
		}
	}

	protected void sendPendingRequests() {
		
		ClientConnectionInstance conn = this.connection;
		
		if (conn == null) {
			return;
		}
		
		Channel channel = conn.getChannel();
		
		if (!channel.isActive()) {
			return;
		}
		
		boolean needFlush = false;
		long seq = synchronizationManager.getCurrentSeq();
		
		while(true) {
		
			TimeoutEntry timeoutEntry = synchronizationManager.getTimeoutEntry(seq);
		
			if (timeoutEntry == null) {
				break;
			}
			
			for (FlexFuture future : timeoutEntry.getFutures()) {
				if (future.send()) {
					needFlush = true;
				}
			}
			
			seq++;
		}
		
		if (needFlush) {
			channel.flush();
		}

	}
	
	private boolean isExpired(Request request, long currentSeq) {
		return currentSeq > request.getTimeoutSeq();
	}
	
	public SynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
	}

	public FlexClientBuilder getSettings() {
		return settings;
	}

	public Executor getWorkerExecutor() {
		return workerExecutor;
	}

	@Override
	public Executor getDefaultExecutor() {
		return workerExecutor;
	}
	
	private void checkStarted() {
		if (!started.get()) {
			throw new IllegalStateException("Must be called after client was started.");
		}
	}
	
	private void checkOnlyStartedOnce() {
		if (!started.compareAndSet(false, true)) {
			throw new IllegalStateException("Client must only be started once.");
		}
	}
	
}
