package alt.flex.client.internal.op;

import com.google.common.base.Function;

import alt.flex.client.api.operation.AbstractOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.client.support.FlexInvalidResponseException;
import alt.flex.protocol.FlexProtocol.Operation;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.RequestType;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseType;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class AbstractSingleOp<O extends AbstractOperation<O,R>, R> extends AbstractOp<O, R> implements Function<OperationResult, R> {

	protected final Operation.Builder operationBuilder = Operation.newBuilder();
	
	public AbstractSingleOp(ClientInstance clientInstance, int storeId) {
		super(clientInstance);
		
		operationBuilder.setStoreId(storeId);
	}
	
	protected Request.Builder newRequestBuilder() {
		
		Request.Builder requestBuilder = Request.newBuilder();
		requestBuilder.setRequestType(RequestType.OPERATION);
		
		requestBuilder.addOperation(operationBuilder.build());
		
		return requestBuilder;
	}
	
	protected Function<Response, R> getResponseTransformer() {
		
		final Function<OperationResult, R> operationTransformer = this;
		
		return new Function<Response, R>() {

			@Override
			public R apply(Response response) {

				if (response.getResponseType() != ResponseType.OPERATION_RESULT) {
					throw new FlexInvalidResponseException("wrong response type", response);
				}
				
				int count = response.getResultCount();
				if (count != 1) {
					throw new FlexInvalidResponseException("expected single operation", response);
				}
				
				OperationResult result = response.getResult(0);
				
				R ret = operationTransformer.apply(result);
				
				fireTraceLogger(response);
				
				return ret;
			}
			
		};
		
	}
}
