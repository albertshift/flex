package alt.flex.client.api;

import alt.flex.protocol.FlexProtocol.Operation;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.ResponseTrace;

/**
 * 
 * @author Albert Shift
 *
 */

public final class RequestTrace {

	private final long timeoutSeq;
	private final int requestNum;
	private final OperationTrace operations[];
	private final long totalClientNanos;
	private final long serverWaitNanos;
	private final long serverExecNanos;
	
	public RequestTrace(Request request, ResponseTrace trace, long totalClientNanos) {	
		
		this.timeoutSeq = request.getTimeoutSeq();
		this.requestNum = request.getRequestNum();
		
		int size = trace.getOperationExecNanosCount();
		this.operations = new OperationTrace[size];
		
		long totalNanos = 0;
		for (int i = 0; i != size; ++i) {
			
			long operationExecNanos = trace.getOperationExecNanos(i);
			Operation operation = request.getOperation(i);
			
			this.operations[i] = new OperationTrace(operation, operationExecNanos);
			
			totalNanos += operationExecNanos;
		}
		
		this.serverWaitNanos = trace.getWaitNanos();
		this.serverExecNanos = totalNanos;
		this.totalClientNanos = totalClientNanos;
	}

	public long getTimeoutSeq() {
		return timeoutSeq;
	}
	
	public int getRequestNum() {
		return requestNum;
	}
	
	public int getOperationCount() {
		return operations.length;
	}
	
	public OperationTrace getOperationTrace(int i) {
		return operations[i];
	}
	
	public long getServerWaitNanos() {
		return serverWaitNanos;
	}

	public long getServerExecNanos() {
		return serverExecNanos;
	}

	public long getTotalServerNanos() {
		return serverWaitNanos + serverExecNanos;
	}

	public long getTotalClientNanos() {
		return totalClientNanos;
	}

	public long getTotalNetworkNanos() {
		return getTotalClientNanos() - getTotalServerNanos();
	}

	@Override
	public String toString() {
		return "RequestTrace [timeoutSeq=" + timeoutSeq + ", requestNum=" + requestNum + ", totalClientNanos="
				+ totalClientNanos + ", serverWaitNanos=" + serverWaitNanos + ", serverExecNanos=" + serverExecNanos
				+ "]";
	}

}
