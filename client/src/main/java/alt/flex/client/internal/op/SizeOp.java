package alt.flex.client.internal.op;

import alt.flex.client.api.operation.SizeOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexWrongResultException;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.OperationType;
import alt.flex.protocol.FlexProtocol.ReturnType;
import alt.flex.protocol.FlexProtocol.ValueOrNull;

/**
 * 
 * @author Albert Shift
 *
 */

public final class SizeOp extends AbstractSingleOp<SizeOperation, Long> implements SizeOperation {

	public SizeOp(ClientInstance clientInstance, int storeId) {
		super(clientInstance, storeId);

		operationBuilder.setOperationType(OperationType.SIZE);
	}
	
	@Override
	public Long apply(OperationResult result) {

		if (result.getReturnType() != ReturnType.SINGLE_VALUE) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		int count = result.getValueCount();
		
		if (count != 1) {
			throw new FlexWrongResultException("expected single value", result);
		}
		
		ValueOrNull val = result.getValue(0);
			
		if (!val.hasLongValue()) {
			throw new FlexWrongResultException("expected long value", result);
		}
		
		return val.getLongValue();
	}

	
}
