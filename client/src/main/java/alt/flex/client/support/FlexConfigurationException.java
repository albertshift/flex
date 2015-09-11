package alt.flex.client.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexConfigurationException extends FlexRuntimeException {

	private static final long serialVersionUID = 1582073442657103456L;

	public FlexConfigurationException(String message) {
		super(message);
	}

	public FlexConfigurationException(Throwable t) {
		super(t);
	}

	public FlexConfigurationException(String message, Throwable t) {
		super(message, t);
	}

}
