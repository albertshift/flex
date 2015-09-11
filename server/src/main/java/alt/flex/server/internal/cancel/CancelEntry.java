package alt.flex.server.internal.cancel;

import alt.flex.support.timewindow.Collectable;

/**
 * 
 * @author Albert Shift
 *
 */

public interface CancelEntry extends Collectable {

	boolean isCanceled(ClientRequestId clientRequestId);
	
	void cancel(ClientRequestId clientRequestId);
	
}
