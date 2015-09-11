package alt.flex.client.internal.op;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import alt.flex.client.api.RequestTrace;
import alt.flex.client.api.TraceLogger;
import alt.flex.client.api.operation.AbstractOperation;
import alt.flex.client.internal.CancellableAndSendable;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexExecutionException;
import alt.flex.client.support.FlexInvalidResponseException;
import alt.flex.client.support.FlexRuntimeException;
import alt.flex.client.support.FlexTimeoutException;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.RequestType;
import alt.flex.protocol.FlexProtocol.Response;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class AbstractOp<O extends AbstractOperation<O,R>, R> implements AbstractOperation<O,R>, CancellableAndSendable {

	private final ClientInstance clientInstance;
	private int timeoutMls;
	private final int timeWindowMls;
	
	private boolean enableTrace;
	private TraceLogger traceLogger;
	
	private volatile long startRequestNanos;
	private volatile Request request;
	private volatile boolean sent;
	
	protected AbstractOp(ClientInstance clientInstance) {
		this.clientInstance = clientInstance;
		this.timeoutMls = clientInstance.getSettings().getDefaultTimeoutMls();
		this.timeWindowMls = clientInstance.getSettings().getTimeoutWindowMls();
	}
	
	protected abstract Request.Builder newRequestBuilder();
	
	protected abstract Function<Response, R> getResponseTransformer();
	
	public R sync() {
		
		ListenableFuture<R> future = doAsync(MoreExecutors.directExecutor());
		
		try {
			return future.get();
		} catch (ExecutionException e) {
			
			Throwable t = e.getCause();
			if (t != null && t instanceof FlexTimeoutException) {
				throw new FlexTimeoutException(e);
			}
			else {
				throw new FlexExecutionException(e);
			}
			
		}
    catch (InterruptedException e) {
    	throw new FlexExecutionException(e);
    }
	
	}
	
	public ListenableFuture<R> async() {
		
		return doAsync(clientInstance.getWorkerExecutor());

	}
	
	protected ListenableFuture<R> doAsync(Executor executor) {
		
		try {
			return doAsyncInternal(executor);
		}
		catch(RuntimeException e) {
			
			if (e instanceof FlexRuntimeException) {
				throw e;
			}
			else {
				throw new FlexRuntimeException(e);
			}
			
		}
		
	}
	
	protected ListenableFuture<R> doAsyncInternal(Executor executor) {
		
		this.startRequestNanos = enableTrace ? System.nanoTime() : 0;
		
		Request.Builder requestBuilder = newRequestBuilder();
		
		if (enableTrace) {
			requestBuilder.setEnableTrace(true);
		}
		
		ListenableFuture<Response> future = clientInstance.getSynchronizationManager().registerRequest(requestBuilder, timeoutMls, this);
		
		this.request = requestBuilder.build();
		
		send(false);
		
		return Futures.transform(future, getResponseTransformer(), executor);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public O setTimeoutMls(int timeoutMls) {
		
		if (timeoutMls <= 0) {
			throw new IllegalArgumentException("timeoutMls less or equal zero");
		}
		
		if (timeoutMls >= timeWindowMls) {
			throw new IllegalArgumentException("timoutMls greater or equal timeWindowMls");
		}

		this.timeoutMls = timeoutMls;
		return (O) this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public O enableTrace(TraceLogger traceLogger) {
		
		this.enableTrace = true;
		this.traceLogger = traceLogger;
		
		return (O) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public O disableTrace() {
		
		this.enableTrace = true;
		this.traceLogger = null;
		
		return (O) this;
	}

	@Override
	public void cancel() {
		
		if (request != null) {
			
			Request.Builder cancelRequest = Request.newBuilder();
			
			cancelRequest.setRequestType(RequestType.CANCEL_OPERATION);
			cancelRequest.setTimeoutSeq(request.getTimeoutSeq());
			cancelRequest.setRequestNum(request.getRequestNum());
			
			clientInstance.send(cancelRequest.build(), false);
			
		}

	}
	
	@Override
	public boolean send(boolean checkExpiration) {
		
		if (request != null && !sent) {
			
			this.sent = clientInstance.send(request, checkExpiration);
			
			return this.sent;
		}
		
		return false;
		
	}
	
	protected void fireTraceLogger(Response response) {
		
		if (enableTrace && traceLogger != null) {
			
			if (!response.hasTrace()) {
				throw new FlexInvalidResponseException("no trace information", response);
			}
			
			if (request.getOperationCount() != response.getTrace().getOperationExecNanosCount()) {
				throw new FlexInvalidResponseException("wrong number of operations", response);
			}

			long totalClientNanos = System.nanoTime() - startRequestNanos;

			RequestTrace entry = new RequestTrace(request, response.getTrace(), totalClientNanos);
			traceLogger.log(entry);
			
		}
		
	}
	
}
