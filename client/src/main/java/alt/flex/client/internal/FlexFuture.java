package alt.flex.client.internal;

import com.google.common.util.concurrent.AbstractFuture;

import alt.flex.client.support.FlexTimeoutException;
import alt.flex.protocol.FlexProtocol.Response;

/**
 * 
 * @author Albert Shift
 *
 */

public final class FlexFuture extends AbstractFuture<Response> {

	protected long p1, p2, p3, p4, p5, p6, p7;
	
	private final CancellableAndSendable operation;
	
  public static FlexFuture create(CancellableAndSendable operation) {
    return new FlexFuture(operation);
  }
  
  private FlexFuture(CancellableAndSendable operation) {
  	this.operation = operation;
  }
  
	@Override
  public boolean set(Response value) {
    return super.set(value);
  }
  
  @Override
  public boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }
  
  public boolean setTimeout() {
  	return super.setException(new FlexTimeoutException("timeout"));
  }

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		
		if (mayInterruptIfRunning) {
			operation.cancel();
		}
		
		return super.cancel(mayInterruptIfRunning);
	}
	
	public boolean send() {
		
		return operation.send(true);
		
	}
	
}
