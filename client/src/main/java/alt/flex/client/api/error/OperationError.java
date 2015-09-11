package alt.flex.client.api.error;

import alt.flex.protocol.ErrorCodes;
import alt.flex.protocol.FlexProtocol.ErrorInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class OperationError {
	
	public static OperationError create(ErrorInformation err) {

		if (err.hasCode()) {
			
			switch(err.getCode()) {
			
			case ErrorCodes.STORE_NOT_FOUND:
				return StoreNotFoundError.INSTANCE;
			
			case ErrorCodes.STORE_EXCEPTION:
				return new StoreException(err);

			case ErrorCodes.STORE_DNOT_SUPPORT_OPERATION:
				return StoreDnotSupportOperationError.INSTANCE;
				
			case ErrorCodes.STORE_UNKNOWN_OPERATION:
				return StoreUnknownOperationError.INSTANCE;
				
			default:
				return new UnknownError(err);
			}
			
		}
		else {
			return new OperationException(err);
		}
	}
	
}
