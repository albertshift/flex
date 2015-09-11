package alt.flex.client.api.error;

import alt.flex.protocol.FlexProtocol.ErrorInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public class OperationException extends OperationError {
	
  private final String message;
  private final String className;
  private final String stackTrace;
  
  public OperationException(ErrorInformation err) {
  	this.message = err.getMessage();
  	this.className = err.getClassName();
  	this.stackTrace = err.getStackTrace();
  }

	public String getMessage() {
		return message;
	}

	public String getClassName() {
		return className;
	}

	public String getStackTrace() {
		return stackTrace;
	}
  
}