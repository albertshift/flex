package alt.flex.client.api.error;

import alt.flex.protocol.FlexProtocol.ErrorInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public final class UnknownError extends OperationException {

	private final int errorCode;
	
	public UnknownError(ErrorInformation err) {
		super(err);
		this.errorCode = err.getCode();
	}

	public int getErrorCode() {
		return errorCode;
	}

}
