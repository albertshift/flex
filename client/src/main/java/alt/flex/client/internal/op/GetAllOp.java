package alt.flex.client.internal.op;

import com.google.protobuf.ByteString;

import alt.flex.client.api.operation.GetAllOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexWrongResultException;
import alt.flex.protocol.FlexProtocol.KeyValuePair;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.OperationType;
import alt.flex.protocol.FlexProtocol.ReturnType;
import alt.flex.protocol.FlexProtocol.ValueOrNull;

/**
 * 
 * @author Albert Shift
 *
 */

public final class GetAllOp extends AbstractSingleOp<GetAllOperation, ByteString[]> implements GetAllOperation {
	
	public GetAllOp(ClientInstance clientInstance, int storeId) {
		super(clientInstance, storeId);
		operationBuilder.setOperationType(OperationType.GET);
	}

	@Override
	public ByteString[] apply(OperationResult result) {

		if (result.getReturnType() != ReturnType.MULTIPLE_VALUES) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		int count = result.getValueCount();
		
		ByteString[] array = new ByteString[count];
		
		for (int i = 0; i != count; ++i) {
			
			ValueOrNull val = result.getValue(i);
			
			array[i] = val.hasValue() ? val.getValue() : null;
			
		}
		
		return array;
	}

	@Override
	public GetAllOperation add(String key) {
		
		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		
		KeyValuePair.Builder kvBuilder = KeyValuePair.newBuilder();
		kvBuilder.setKey(key);
		
		operationBuilder.addKeyValue(kvBuilder.build());
		
		return this;
	}
	
}
