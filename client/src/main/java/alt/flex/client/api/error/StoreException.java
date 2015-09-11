package alt.flex.client.api.error;

import alt.flex.protocol.FlexProtocol.ErrorInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public final class StoreException extends OperationException {

	public StoreException(ErrorInformation err) {
		super(err);
	}
	
}
