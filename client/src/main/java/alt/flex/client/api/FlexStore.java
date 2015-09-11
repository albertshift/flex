package alt.flex.client.api;

import com.google.protobuf.ByteString;

import alt.flex.client.api.operation.ClearOperation;
import alt.flex.client.api.operation.GetAllOperation;
import alt.flex.client.api.operation.GetOperation;
import alt.flex.client.api.operation.PutAllOperation;
import alt.flex.client.api.operation.PutOperation;
import alt.flex.client.api.operation.SizeOperation;

/**
 * 
 * @author Albert Shift
 *
 */

public interface FlexStore {

	int getStoreId();
	
	String getStoreName();
	
	GetOperation get(String key);
	
	GetAllOperation getAll();
	
	PutOperation put(String key, ByteString value);
	
	PutAllOperation putAll();
	
	SizeOperation size();
	
	ClearOperation clear();
	
}
