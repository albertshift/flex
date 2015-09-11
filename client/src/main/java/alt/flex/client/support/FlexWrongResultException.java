package alt.flex.client.support;

import alt.flex.protocol.FlexProtocol.OperationResult;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexWrongResultException extends FlexExecutionException {

	private static final long serialVersionUID = -5786040527159775795L;
	
	private final OperationResult result;
	
	public FlexWrongResultException(String message, OperationResult result) {
		super(message);
		this.result = result;
	}

	public FlexWrongResultException(Throwable t, OperationResult result) {
		super(t);
		this.result = result;
	}

	public FlexWrongResultException(String message, Throwable t, OperationResult result) {
		super(message, t);
		this.result = result;
	}

	public OperationResult getResult() {
		return result;
	}
	
}
