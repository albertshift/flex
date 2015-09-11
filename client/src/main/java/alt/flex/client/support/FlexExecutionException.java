package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexExecutionException extends FlexRuntimeException {

	private static final long serialVersionUID = -8308834627900574601L;

	public FlexExecutionException(String message) {
		super(message);
	}

	public FlexExecutionException(Throwable t) {
		super(t);
	}

	public FlexExecutionException(String message, Throwable t) {
		super(message, t);
	}

}
