package alt.flex.server;

import java.util.concurrent.ExecutorService;

import alt.flex.server.internal.ServerInstance;
import alt.flex.server.support.ConfigurationException;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexServerBuilder {

	private boolean inUse = false;

	private int port = -1;
	private int nioThreads = 1;
	private boolean ssl = false;
	private int heartBreathIntervalMls = 60000;
  private int maxIncomingConnections = 100;
  private int cancelWindowSec = 128;
  private ExecutorService workerExecutor = null;
	
	public FlexServerBuilder() {
	}

	public FlexServerBuilder(int port) {
		
		if (port <= 0) {
			throw new IllegalArgumentException("port less or equal zero");
		}
		
		this.port = port;
	}

	public FlexServerBuilder setPort(int port) {
		
		if (port <= 0) {
			throw new IllegalArgumentException("port less or equal zero");
		}
		
		ensureNotInUse();
		this.port = port;
		return this;
	}

	public FlexServerBuilder setNioThreads(int nThreads) {
		
		if (nThreads <= 0) {
			throw new IllegalArgumentException("nThreads less or equal zero");
		}
		
		ensureNotInUse();
		this.nioThreads = nThreads;
		return this;
	}
	
	public FlexServerBuilder setWorkerExecutor(ExecutorService executor) {
		
		if (executor == null) {
			throw new IllegalArgumentException("null executor");
		}
		
		ensureNotInUse();
		this.workerExecutor = executor;
		return this;
	}

	public FlexServerBuilder useSsl(boolean ssl) {
		ensureNotInUse();
		this.ssl = ssl;
		return this;
	}

	public FlexServerBuilder setHeartBreathIntervalMls(int intervalMls) {
		
		if (intervalMls <= 0) {
			throw new IllegalArgumentException("intervalMls less or equal zero");
		}
		
		ensureNotInUse();
		this.heartBreathIntervalMls = intervalMls;
		return this;
	}
	
	public FlexServerBuilder setMaxIncomingConnections(int maxIncomingConnections) {
		
		if (maxIncomingConnections <= 0) {
			throw new IllegalArgumentException("maxIncomingConnections less or equal zero");
		}
		
		ensureNotInUse();
		this.maxIncomingConnections = maxIncomingConnections;
		return this;
	}
	
	public FlexServerBuilder setCancelWindowSec(int cancelWindowSec) {
		
		if (cancelWindowSec <= 0) {
			throw new IllegalArgumentException("cancelWindowSec less or equal zero");
		}
		
    if ((cancelWindowSec & (cancelWindowSec - 1)) != 0) {
      throw new IllegalArgumentException("cancelWindowSec not a power of two");
    }
		
		ensureNotInUse();
		
		this.cancelWindowSec = cancelWindowSec;
		return this;
	}

	public FlexServer build() {
		ensureNotInUse();
		this.inUse = true;
		return new ServerInstance(this);
	}

	private void ensureNotInUse() {
		if (this.inUse) {
			throw new ConfigurationException("server builder is already in use");
		}
	}

	public int getPort() {
		return port;
	}

	public int getNioThreads() {
		return nioThreads;
	}

	public ExecutorService getWorkerExecutor() {
		return workerExecutor;
	}

	public boolean useSsl() {
		return ssl;
	}

	public int getHeartBreathIntervalMls() {
		return heartBreathIntervalMls;
	}

	public int getMaxIncomingConnections() {
		return maxIncomingConnections;
	}
	
	public int getCancelWindowSec() {
		return cancelWindowSec;
	}


}
