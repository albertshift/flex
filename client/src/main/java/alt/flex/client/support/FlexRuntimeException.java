package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -1629736032810069835L;

	public FlexRuntimeException(String message) {
		super(message);
	}

	public FlexRuntimeException(Throwable t) {
		super(t);
	}

	public FlexRuntimeException(String message, Throwable t) {
		super(message, t);
	}

}
