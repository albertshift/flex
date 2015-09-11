package alt.flex.server.internal;

import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.server.internal.cancel.CancelEntry;
import alt.flex.server.internal.cancel.ClientRequestId;

/**
 * 
 * @author Albert Shift
 *
 */

public final class DefaultRequestCanceler implements RequestCanceler {

	private final CancelEntry cancelEntry;
	private final ClientRequestId clientRequestId;
	private final long requestExpirationTime;

	
	public DefaultRequestCanceler(DefaultClientManager clientManager, ClientInformation info, Request request) {
		
		long clientTimeoutMlsSinceInit = info.getClientTimeoutMlsSinceInit(request.getTimeoutSeq());
		long initServerTimeMls = info.getInitServerTimeMls();
		
		requestExpirationTime = initServerTimeMls + clientTimeoutMlsSinceInit + ServerConstants.DEFAULT_TIMEOUT_LATENCY_MLS;
		long currentTime = System.currentTimeMillis();
		
		if (currentTime >= requestExpirationTime) {
			cancelEntry = null;
			clientRequestId = null;
		}
		else {
			cancelEntry = clientManager.getCancelEntry(initServerTimeMls, clientTimeoutMlsSinceInit);
			clientRequestId = ClientRequestId.create(info.getClientId(), request.getTimeoutSeq(), request.getRequestNum());
		}
		
	}
	
	@Override
	public boolean isCanceledOrTimeout() {
		
		if (System.currentTimeMillis() >= requestExpirationTime) {
			return true;
		}
		
		if (cancelEntry == null || clientRequestId == null) {
			return true;
		}
		
		if (cancelEntry.isCanceled(clientRequestId)) {
			return true;
		}
		
		return false;
	}

	@Override
	public void cancel() {
		
		if (System.currentTimeMillis() >= requestExpirationTime) {
			return;
		}
		
		if (cancelEntry != null && clientRequestId != null) {
			
			cancelEntry.cancel(clientRequestId);
			
		}
		
	}

}
