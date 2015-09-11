package alt.flex.client.internal.op;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

import alt.flex.client.api.error.OperationError;
import alt.flex.client.api.operation.BatchOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexInvalidResponseException;
import alt.flex.protocol.FlexProtocol.ErrorInformation;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.RequestType;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseType;
import alt.flex.protocol.FlexProtocol.ReturnType;

/**
 * 
 * @author Albert Shift
 *
 */

public final class BatchOp extends AbstractOp<BatchOperation, Object[]> implements BatchOperation, Function<Response, Object[]> {

	protected final Request.Builder requestBuilder;
	
	private List<AbstractSingleOp<?, Object>> operations = new ArrayList<AbstractSingleOp<?, Object>>();
	
	public BatchOp(ClientInstance clientInstance) {
		super(clientInstance);
		
		requestBuilder = Request.newBuilder();
		requestBuilder.setRequestType(RequestType.OPERATION);
		
	}

	@Override
	public BatchOperation add(AbstractSingleOp<?, ?> op) {

		@SuppressWarnings("unchecked")
		AbstractSingleOp<?, Object> singleOp = (AbstractSingleOp<?, Object>) op;
		
		operations.add(singleOp);
		
		return this;
	}

	@Override
	protected Request.Builder newRequestBuilder() {

		for (AbstractSingleOp<?, Object> op : operations) {
			requestBuilder.addOperation(op.operationBuilder.build());
		}
		
		return requestBuilder;
	}

	@Override
	protected Function<Response, Object[]> getResponseTransformer() {
		return this;
	}

	@Override
	public Object[] apply(Response response) {
		
		if (response.getResponseType() != ResponseType.OPERATION_RESULT) {
			throw new FlexInvalidResponseException("wrong reponse type", response);
		}
		
		Object[] result = new Object[operations.size()];
		
		int count = response.getResultCount();
		if (count != operations.size()) {
			throw new FlexInvalidResponseException("wrong number of operations", response);
		}
		
		for (int i = 0; i != count; ++i) {
			
			OperationResult operationResult = response.getResult(i);
			AbstractSingleOp<?, Object> op = operations.get(i);
			
			if (operationResult.getReturnType() == ReturnType.ERROR_OR_EXCEPTION) {
				ErrorInformation err = operationResult.getError();
				result[i] = OperationError.create(err);
			}
			else {
				result[i] = op.apply(operationResult);
			}
			
		}
		
		fireTraceLogger(response);
		
		return result;
	}
	
}
