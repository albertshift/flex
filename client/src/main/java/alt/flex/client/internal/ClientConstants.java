package alt.flex.client.internal;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ClientConstants {

	private ClientConstants() {
	}
	
	public static final int CLIENT_VERISON = 1;
	
	public static final int DEFAULT_TIMEOUT_MLS = 30000;

	public static final int DEFAULT_RECONNECT_INTERVAL_MLS = 10000;
	
	public static final int DEFAULT_ALIVE_INTERVAL_MLS = 120000;
	
	/**
	 * Must be pow of 2
	 */
	
	public static final int TIMEOUT_WINDOW_MLS = 32768;
  
}
