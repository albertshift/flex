package alt.flex.client.internal.op;

import alt.flex.client.api.Nothing;
import alt.flex.client.api.operation.PutAllOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexWrongResultException;
import alt.flex.protocol.FlexProtocol.KeyValuePair;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.OperationType;
import alt.flex.protocol.FlexProtocol.ReturnType;

import com.google.protobuf.ByteString;

/**
 * 
 * @author Albert Shift
 *
 */

public final class PutAllOp extends AbstractSingleOp<PutAllOperation, Nothing> implements PutAllOperation {

	public PutAllOp(ClientInstance clientInstance, int storeId) {
		super(clientInstance, storeId);
		operationBuilder.setOperationType(OperationType.PUT);
	}
	
	@Override
	public Nothing apply(OperationResult result) {
		
		if (result.getReturnType() != ReturnType.NOTHING) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		return Nothing.INSTANCE;
	}
	
	public PutAllOperation add(String key, ByteString value) {
		
		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		
		if (value == null) {
			throw new IllegalArgumentException("value can not be null");
		}
		
		KeyValuePair.Builder kvBuilder = KeyValuePair.newBuilder();
		kvBuilder.setKey(key);
		kvBuilder.setValue(value);
		operationBuilder.addKeyValue(kvBuilder.build());
		
		return this;
	}
	
}
