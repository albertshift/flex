package alt.flex.client.api;

/**
 * 
 * @author Albert Shift
 *
 */

public interface LifecycleListener {

	void clientActive();
	
	void clientInactive();
	
	void onConnectionException(Exception e);
	
}
