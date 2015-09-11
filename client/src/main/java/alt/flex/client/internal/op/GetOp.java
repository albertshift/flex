package alt.flex.client.internal.op;

import com.google.protobuf.ByteString;

import alt.flex.client.api.operation.GetOperation;
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

public final class GetOp extends AbstractSingleOp<GetOperation, ByteString> implements GetOperation {

	public GetOp(ClientInstance clientInstance, int storeId, String key) {
		super(clientInstance, storeId);

		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		
		KeyValuePair.Builder kvBuilder = KeyValuePair.newBuilder();
		kvBuilder.setKey(key);
		
		operationBuilder.setOperationType(OperationType.GET);
		operationBuilder.addKeyValue(kvBuilder.build());
		
	}
	
	@Override
	public ByteString apply(OperationResult result) {

		if (result.getReturnType() != ReturnType.SINGLE_VALUE) {
			throw new FlexWrongResultException("wrong result type", result);
		}
		
		int count = result.getValueCount();
		
		if (count != 1) {
			throw new FlexWrongResultException("expected single value", result);
		}
		
		ValueOrNull val = result.getValue(0);
			
		return val.hasValue() ? val.getValue() : null;
	}

	
}
