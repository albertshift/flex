package alt.flex.server.internal.cancel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ConcurrentHashSetCancelEntry implements CancelEntry {

	private final ConcurrentHashMap<ClientRequestId, Boolean> set = new ConcurrentHashMap<ClientRequestId, Boolean>();
	
	@Override
	public boolean isCanceled(ClientRequestId clientRequestId) {
		return set.containsKey(clientRequestId);
	}

	@Override
	public void cancel(ClientRequestId clientRequestId) {
		set.putIfAbsent(clientRequestId, Boolean.TRUE);
	}

	@Override
	public void collect() {

		//if (!set.isEmpty()) {
		//	System.out.println("COLLECT NOT EMPTY " + System.currentTimeMillis() + ", size=" + set.size());
		//}

		set.clear();
	}

}
