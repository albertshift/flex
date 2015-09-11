package alt.flex.client.api.operation;

import com.google.common.util.concurrent.ListenableFuture;

import alt.flex.client.api.Cancellable;
import alt.flex.client.api.TraceLogger;

/**
 * 
 * @author Albert Shift
 *
 */

public interface AbstractOperation<O extends AbstractOperation<O, R>, R> extends Cancellable {
	
	R sync();
	
	ListenableFuture<R> async();
	
	O setTimeoutMls(int timeoutMls);
	
	O enableTrace(TraceLogger logger);
	
	O disableTrace();
	
}
