package alt.flex.client.internal.op;

import java.util.UUID;

import alt.flex.client.internal.ClientConstants;
import alt.flex.client.internal.ClientInstance;
import alt.flex.protocol.FlexProtocol.InitParams;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.Request.Builder;
import alt.flex.support.util.UuidConverter;
import alt.flex.protocol.FlexProtocol.RequestType;

/**
 * 
 * @author Albert Shift
 *
 */

public final class InitOp extends ProtocolOp {

	private final UUID clientId;
	private final long currentSeq;
	private final boolean firstInit;
	
	public InitOp(ClientInstance clientInstance, UUID clientId, long currentSeq, boolean firstInit) {
		super(clientInstance);
		this.clientId = clientId;
		this.currentSeq = currentSeq;
		this.firstInit = firstInit;
	}
	
	@Override
	protected Builder newRequestBuilder() {
		
		InitParams.Builder initBuilder = InitParams.newBuilder();
		initBuilder.setVersion(ClientConstants.CLIENT_VERISON);
		initBuilder.setClientId(UuidConverter.toFlexUuid(clientId));
		initBuilder.setInitSeq(currentSeq);
		initBuilder.setGetStores(firstInit);

		Request.Builder requestBuilder = Request.newBuilder();
		requestBuilder.setRequestType(RequestType.INIT);
		requestBuilder.setInit(initBuilder.build());
		
		return requestBuilder;
	}

}
