package alt.flex.client;

import java.util.concurrent.Executor;

import alt.flex.client.api.FlexStoreManager;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class FlexClient implements FlexStoreManager {

	public static FlexClientBuilder newBuilder() {
		return new FlexClientBuilder();
	}
	
	public abstract void start();

	public abstract boolean isActive();

	public abstract void shutdown();
	
	public abstract Executor getDefaultExecutor();
	
	
}
