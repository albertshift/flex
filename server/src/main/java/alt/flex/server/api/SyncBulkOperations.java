package alt.flex.server.api;

import com.google.protobuf.ByteString;

/**
 * 
 * @author Albert Shift
 *
 */

public interface SyncBulkOperations {

	ByteString[] getAll(String[] keys);
	
	void putAll(String[] keys, ByteString[] values);

	void removeAll(String[] keys);
	
}
