package alt.flex.server.api;

import com.google.protobuf.ByteString;

/**
 * 
 * @author Albert Shift
 *
 */

public interface SyncAtomicOperations {

	ByteString get(String key);
	
	void put(String key, ByteString value);

	void remove(String key);
	
	void clear();
	
	long size();
	
}
