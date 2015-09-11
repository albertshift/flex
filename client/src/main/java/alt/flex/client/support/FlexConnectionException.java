package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexConnectionException extends FlexRuntimeException {

	private static final long serialVersionUID = 8805307397567951690L;

	public FlexConnectionException(String message) {
		super(message);
	}

	public FlexConnectionException(Throwable t) {
		super(t);
	}

	public FlexConnectionException(String message, Throwable t) {
		super(message, t);
	}

}
