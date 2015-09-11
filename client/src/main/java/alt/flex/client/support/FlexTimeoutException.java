package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexTimeoutException extends FlexRuntimeException {

	private static final long serialVersionUID = -3179990962508917657L;

	public FlexTimeoutException(String message) {
		super(message);
	}

	public FlexTimeoutException(Throwable t) {
		super(t);
	}

	public FlexTimeoutException(String message, Throwable t) {
		super(message, t);
	}

}
