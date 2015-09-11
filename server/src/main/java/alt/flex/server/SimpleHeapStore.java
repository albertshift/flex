package alt.flex.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ByteString;

import alt.flex.server.api.SimpleFlexStore;

/**
 * 
 * @author Albert Shift
 *
 */

public class SimpleHeapStore implements SimpleFlexStore {

	private final Map<String, ByteString> map = new ConcurrentHashMap<String, ByteString>();
	
	@Override
	public ByteString get(String key) {
		return map.get(key);
	}

	@Override
	public void put(String key, ByteString value) {
		map.put(key, value);
	}

	@Override
	public void remove(String key) {
		map.remove(key);
	}
	
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public long size() {
		return map.size();
	}
	
}
