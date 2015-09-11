package alt.flex.client.api.operation;

import com.google.protobuf.ByteString;


/**
 * 
 * @author Albert Shift
 *
 */

public interface GetAllOperation extends AbstractOperation<GetAllOperation, ByteString[]> {
	
	GetAllOperation add(String key);
	
}
