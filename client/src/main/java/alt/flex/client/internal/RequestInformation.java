package alt.flex.client.internal;

import com.google.common.util.concurrent.ListenableFuture;

import alt.flex.client.support.FlexTimeoutException;
import alt.flex.protocol.FlexProtocol.Response;

/**
 * 
 * @author Albert Shift
 *
 */

public class RequestInformation {

	private final long expiration;
	private final FlexFuture future;
	
	public RequestInformation(long timeout) {
		this.expiration = System.currentTimeMillis() + timeout;
		this.future = FlexFuture.create(null);
	}
	
	public boolean isExpired() {
		return System.currentTimeMillis() > expiration;
	}
	
	public void setResult(Response obj) {
		future.set(obj);
	}

	public ListenableFuture<Response> getFuture() {
		return future;
	}
	
	public void setTimeoutException() {
		future.setException(new FlexTimeoutException("timeout"));
	}
	
}
