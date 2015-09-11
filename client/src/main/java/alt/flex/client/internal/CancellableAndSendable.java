package alt.flex.client.internal;

import alt.flex.client.api.Cancellable;

/**
 * 
 * @author Albert Shift
 *
 */

public interface CancellableAndSendable extends Cancellable {

	boolean send(boolean checkExpiration);
	
}
