package alt.flex.client.internal.op;

import alt.flex.client.api.Nothing;
import alt.flex.client.api.operation.ClearOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexWrongResultException;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.OperationType;
import alt.flex.protocol.FlexProtocol.ReturnType;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ClearOp extends AbstractSingleOp<ClearOperation, Nothing> implements ClearOperation {

	public ClearOp(ClientInstance clientInstance, int storeId) {
		super(clientInstance, storeId);

		operationBuilder.setOperationType(OperationType.CLEAR);
	}
	
	@Override
	public Nothing apply(OperationResult result) {

		if (result.getReturnType() != ReturnType.NOTHING) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		return Nothing.INSTANCE;
		
	}

	
}
