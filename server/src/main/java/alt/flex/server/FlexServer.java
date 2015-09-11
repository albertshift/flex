package alt.flex.server;

import alt.flex.server.api.AbstractFlexStore;

public abstract class FlexServer {

	public static FlexServerBuilder newBuilder() {
		return new FlexServerBuilder();
	}
	
	public abstract int addStore(String name, AbstractFlexStore store);
	
	public abstract void start() throws InterruptedException;
	
	public abstract void join() throws InterruptedException;
	
	public abstract void shutdown();
	
}
