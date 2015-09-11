package alt.flex.server.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import alt.flex.protocol.FlexProtocol;
import alt.flex.protocol.FlexProtocol.StoreInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public class StoreNameManager {

	private final AtomicInteger nextStoreNumber = new AtomicInteger(ServerConstants.START_STORE_ID);
	
	private final ConcurrentMap<String, Integer> nameToNumber = new ConcurrentHashMap<String, Integer>();
	
	public void addStorePayloads(FlexProtocol.Response.Builder response) {
		
		for (Map.Entry<String, Integer> entry : nameToNumber.entrySet()) {
			
			StoreInformation.Builder store = StoreInformation.newBuilder();
			store.setStoreId(entry.getValue());
			store.setName(entry.getKey());
			
			response.addStore(store.build());
			
		}
	}
	
	public int registerStore(String name) {
		
		int storeId = nextStoreNumber.getAndIncrement();
		
		Integer num = nameToNumber.putIfAbsent(name, storeId);
		if (num != null) {
			throw new IllegalStateException("store " + name + " was already registered under the number " + num);
		}
		
		return storeId;
	}
	
}
