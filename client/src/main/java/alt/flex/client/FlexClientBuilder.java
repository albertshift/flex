package alt.flex.client;

import java.util.concurrent.Executor;

import alt.flex.client.api.LifecycleListener;
import alt.flex.client.internal.ClientConstants;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexConfigurationException;


/**
 * 
 * @author Albert Shift
 *
 */

public class FlexClientBuilder {

	private boolean inUse = false;

	private int nioThreads = 1;
	private String host;
	private int port = -1;
	private boolean ssl = false;
	private boolean reconnect = true;
	private int defaultTimeoutMls = ClientConstants.DEFAULT_TIMEOUT_MLS;
	private int timeoutWindowMls = ClientConstants.TIMEOUT_WINDOW_MLS;
	private Executor workerExecutor = null;
	private int reconnectIntervalMls = ClientConstants.DEFAULT_RECONNECT_INTERVAL_MLS;

	private LifecycleListener lifecycleListener;
	
	public FlexClientBuilder() {
	}

	public FlexClientBuilder setNioThreads(int nThreads) {
		
		if (nThreads <= 0) {
			throw new IllegalArgumentException("invalid nThreads=" + nThreads);
		}
		
		ensureNotInUse();
		this.nioThreads = nThreads;
		return this;
	}

	public FlexClientBuilder setWorkerExecutor(Executor executor) {
		
		if (executor == null) {
			throw new IllegalArgumentException("invalid executor=" + executor);
		}
		
		ensureNotInUse();
		this.workerExecutor = executor;
		return this;
	}
	
	public FlexClientBuilder setHost(String host) {
		
		if (host == null) {
			throw new IllegalArgumentException("invalid host=" + host);
		}
		
		ensureNotInUse();
		this.host = host;
		return this;
	}

	public FlexClientBuilder setPort(int port) {
		
		if (port <= 0) {
			throw new IllegalArgumentException("invalid port=" + nioThreads);
		}
		
		ensureNotInUse();
		this.port = port;
		return this;
	}

	public FlexClientBuilder useSsl(boolean ssl) {
		ensureNotInUse();
		this.ssl = ssl;
		return this;
	}

	public FlexClientBuilder useReconnect(boolean reconnect) {
		ensureNotInUse();
		this.reconnect = reconnect;
		return this;
	}

	public FlexClientBuilder setDefaultTimeoutMls(int timeoutMls) {
		
		if (timeoutMls <= 0) {
			throw new IllegalArgumentException("timeoutMls less or equal zero");
		}
		
		ensureNotInUse();
		this.defaultTimeoutMls = timeoutMls;
		return this;
	}

	public FlexClientBuilder setReconnectIntervalMls(int reconnectIntervalMls) {
		
		if (reconnectIntervalMls <= 0) {
			throw new IllegalArgumentException("reconnectIntervalMls less or equal zero");
		}
		
		ensureNotInUse();
		this.reconnectIntervalMls = reconnectIntervalMls;
		
		return this;
	}

	public FlexClientBuilder setTimeoutWindowMls(int timeoutWindowMls) {

		if (timeoutWindowMls <= 0) {
			throw new IllegalArgumentException("timeoutWindowMls less or equal zero");
		}
		
    if ((timeoutWindowMls & (timeoutWindowMls - 1)) != 0) {
      throw new IllegalArgumentException("timeoutWindowMls not a power of two");
    }
		
		ensureNotInUse();
		this.timeoutWindowMls = timeoutWindowMls;
		return this;
	}

	public FlexClientBuilder setLifecycleListener(LifecycleListener lifecycleListener) {
		
		if (lifecycleListener == null) {
			throw new IllegalArgumentException("lifecycleListener can not be null");
		}
		
		ensureNotInUse();
		this.lifecycleListener = lifecycleListener;
		return this;
	}

	public LifecycleListener getLifecycleListener() {
		return lifecycleListener;
	}

	public Executor getWorkerExecutor() {
		return workerExecutor;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean useSsl() {
		return ssl;
	}

	public boolean useReconnect() {
		return reconnect;
	}

	public int getDefaultTimeoutMls() {
		return defaultTimeoutMls;
	}
	
	public int getTimeoutWindowMls() {
		return timeoutWindowMls;
	}
	
	public int getReconnectIntervalMls() {
		return reconnectIntervalMls;
	}

	public int getNioThreads() {
		return nioThreads;
	}

	public FlexClient build() {
		ensureNotInUse();
		
		if (this.defaultTimeoutMls >= this.timeoutWindowMls) {
			throw new IllegalArgumentException("timeoutWindowMls less or equal defaultTimeoutMls");
		}
		
		this.inUse = true;
		return new ClientInstance(this);
	}

	private void ensureNotInUse() {
		if (this.inUse) {
			throw new FlexConfigurationException("client builder is already in use");
		}
	}
}
