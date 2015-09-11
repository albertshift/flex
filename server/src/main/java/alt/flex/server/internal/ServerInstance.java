package alt.flex.server.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLException;

import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.server.FlexServer;
import alt.flex.server.FlexServerBuilder;
import alt.flex.server.api.AbstractFlexStore;
import alt.flex.server.internal.io.DefaultServerInitializer;
import alt.flex.server.support.ConfigurationException;

/**
 * 
 * @author Albert Shift
 *
 */

public class ServerInstance extends FlexServer {

	private final FlexServerBuilder settings;
	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private volatile Channel channel;

	private final AtomicBoolean started = new AtomicBoolean();
	
	private final StoreNameManager storeNameManager = new StoreNameManager();
	private final StoreReferenceManager storeReferenceManager = new StoreReferenceManager();
	
	private final ServerTimer serverTimer = new ServerTimer();

	private final ExecutorService workerExecutor;
	
	private final ClientManager clientManager;
	
	public ServerInstance(FlexServerBuilder settings) {
		this.settings = settings;
		this.bossGroup = new NioEventLoopGroup(settings.getNioThreads());
		this.workerGroup = new NioEventLoopGroup(settings.getNioThreads());
		
		this.workerExecutor = settings.getWorkerExecutor() != null ? settings.getWorkerExecutor() : Executors.newCachedThreadPool();
		
		this.clientManager = new DefaultClientManager(settings);
	}

	@Override
	public int addStore(String name, AbstractFlexStore store) {
		
		checkNotStarted();
		
		int storeId = storeNameManager.registerStore(name);
		
		storeReferenceManager.setStore(storeId, store);
		
		return storeId;
	}

	@Override
	public void start() throws InterruptedException {

		checkOnlyStartedOnce();
		
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new DefaultServerInitializer(this, configureSsl()))
				.option(ChannelOption.SO_BACKLOG, settings.getMaxIncomingConnections())
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		this.channel = b.bind(settings.getPort()).sync().channel();

		HeartBreathSender sender = new HeartBreathSender(clientManager);
		serverTimer.schedule(sender, settings);

		this.clientManager.start(workerExecutor);
	}

	@Override
	public void join() throws InterruptedException {

		checkStarted();
		
		this.channel.closeFuture().sync();

	}

	@Override
	public void shutdown() {

		serverTimer.shutdown();
		
		if (this.channel != null) {
			this.channel.close();
			this.channel = null;
		}
		
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		
		workerExecutor.shutdown();

	}

	private SslContext configureSsl() {
		if (settings.useSsl()) {
			SelfSignedCertificate ssc;
			try {
				ssc = new SelfSignedCertificate();
			} catch (CertificateException e) {
				throw new ConfigurationException("fail to create self signed certificate", e);
			}
			try {
				return SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
			} catch (SSLException e) {
				throw new ConfigurationException("fail to create ssl context", e);
			}
		}
		return null;
	}

	public void submitOperation(ChannelHandlerContext ctx, Request request) {
		
		SimpleRequestHandler handler = new SimpleRequestHandler(ctx, request, storeReferenceManager, clientManager);
		
		workerExecutor.submit(handler);
		
	}
	
	public void cancelOperation(ChannelHandlerContext ctx, Request request) {
		
		RequestCanceler canceler = clientManager.findRequestCanceler(ctx.channel(), request);
		if (canceler != null) {
			
			canceler.cancel();
			
		}
		
	}

	public StoreNameManager getStoreNameManager() {
		return storeNameManager;
	}

	public ClientManager getClientManager() {
		return clientManager;
	}

	public FlexServerBuilder getSettings() {
		return settings;
	}

	private void checkNotStarted() {
		if (started.get()) {
			throw new IllegalStateException("Must be called before starting the server.");
		}
	}
	
	private void checkStarted() {
		if (!started.get()) {
			throw new IllegalStateException("Must be called after server was started.");
		}
	}
	
	private void checkOnlyStartedOnce() {
		if (!started.compareAndSet(false, true)) {
			throw new IllegalStateException("Server must only be started once.");
		}
	}
	
}
