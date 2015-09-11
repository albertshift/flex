package alt.flex.client.internal;

import alt.flex.client.api.LifecycleListener;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ConnectTask implements Runnable {

	private final ClientInstance clientInstance;
	private final boolean startup;
	
	public ConnectTask(ClientInstance clientInstance, boolean startup) {
		this.clientInstance = clientInstance;
		this.startup = startup;
	}
	
	@Override
	public void run() {
		
		LifecycleListener lifecycleListener = clientInstance.getSettings().getLifecycleListener();
		
		if (!startup && lifecycleListener != null) {
			lifecycleListener.clientInactive();
		}
		
		boolean skipSleep = startup;
		
		while(!Thread.interrupted()) {
		
			if (!skipSleep) {
				
				try {
					Thread.sleep(clientInstance.getSettings().getReconnectIntervalMls());
				} catch (InterruptedException e) {
					break;
				}
				
			}
			
			skipSleep = false;
			
			try {
				clientInstance.connect();
			}
			catch(Exception e) {
				if (lifecycleListener != null) {
					lifecycleListener.onConnectionException(e);
				}
				continue;
			}
			
			clientInstance.sendPendingRequests();
			
			if (lifecycleListener != null) {
				lifecycleListener.clientActive();
			}
			break;
		
		}
	}

}
