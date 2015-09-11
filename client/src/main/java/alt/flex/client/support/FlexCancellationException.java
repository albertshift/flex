package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexCancellationException extends FlexRuntimeException {

	private static final long serialVersionUID = -1878987713279923673L;

	public FlexCancellationException(String message) {
		super(message);
	}

	public FlexCancellationException(Throwable t) {
		super(t);
	}

	public FlexCancellationException(String message, Throwable t) {
		super(message, t);
	}

}
