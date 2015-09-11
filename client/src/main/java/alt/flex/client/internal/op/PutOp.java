package alt.flex.client.internal.op;

import alt.flex.client.api.Nothing;
import alt.flex.client.api.operation.PutOperation;
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

public final class PutOp extends AbstractSingleOp<PutOperation, Nothing> implements PutOperation {

	public PutOp(ClientInstance clientInstance, int storeId, String key, ByteString value) {
		super(clientInstance, storeId);
		
		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		
		if (value == null) {
			throw new IllegalArgumentException("value can not be null");
		}
		
		operationBuilder.setOperationType(OperationType.PUT);

		KeyValuePair.Builder kvBuilder = KeyValuePair.newBuilder();
		kvBuilder.setKey(key);
		kvBuilder.setValue(value);
		operationBuilder.addKeyValue(kvBuilder.build());
		
	}

	@Override
	public Nothing apply(OperationResult result) {
		
		if (result.getReturnType() != ReturnType.NOTHING) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		return Nothing.INSTANCE;
	}
	
}
