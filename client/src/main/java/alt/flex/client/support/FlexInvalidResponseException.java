package alt.flex.client.support;

import alt.flex.protocol.FlexProtocol.Response;

/**
 * 
 * @author Albert Shift
 *
 */

public class FlexInvalidResponseException extends FlexExecutionException {

	private static final long serialVersionUID = -8580624432064875934L;

	private final Response response;
	
	public FlexInvalidResponseException(String message, Response response) {
		super(message);
		this.response = response;
	}

	public FlexInvalidResponseException(Throwable t, Response response) {
		super(t);
		this.response = response;
	}

	public FlexInvalidResponseException(String message, Throwable t, Response response) {
		super(message, t);
		this.response = response;
	}

	public Response getResponse() {
		return response;
	}
	
}
