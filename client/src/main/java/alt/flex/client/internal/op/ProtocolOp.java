package alt.flex.client.internal.op;

import alt.flex.client.api.operation.ProtocolOperation;
import alt.flex.client.internal.ClientInstance;
import alt.flex.protocol.FlexProtocol.Response;

import com.google.common.base.Function;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class ProtocolOp extends AbstractOp<ProtocolOperation, Response> implements ProtocolOperation, Function<Response, Response> {

	public ProtocolOp(ClientInstance clientInstance) {
		super(clientInstance);
	}

	@Override
	public Response apply(Response response) {
		fireTraceLogger(response);
		return response;
	}
	
	@Override
	protected Function<Response, Response> getResponseTransformer() {
		return this;
	}
	
}
