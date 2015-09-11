package alt.flex.client.api;

import alt.flex.protocol.FlexProtocol.Operation;
import alt.flex.protocol.FlexProtocol.OperationType;

/**
 * 
 * @author Albert Shift
 *
 */

public final class OperationTrace {

	private final int storeId;
	private final OperationType operationType;
	private final long operationExecNanos;
	
	public OperationTrace(Operation operation, long operationExecNanos) {
		this.storeId = operation.getStoreId();
		this.operationType = operation.getOperationType();
		this.operationExecNanos = operationExecNanos;
	}

	public int getStoreId() {
		return storeId;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public long getOperationExecNanos() {
		return operationExecNanos;
	}

	@Override
	public String toString() {
		return "OperationTrace [storeId=" + storeId + ", operationType=" + operationType
				+ ", operationExecNanos=" + operationExecNanos + "]";
	}
	
}
