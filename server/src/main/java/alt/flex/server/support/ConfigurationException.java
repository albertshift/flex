package alt.flex.server.support;

/**
 * 
 * @author Albert Shift
 *
 */

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -3179990962508917657L;

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable t) {
		super(t);
	}

	public ConfigurationException(String message, Throwable t) {
		super(message, t);
	}

}
