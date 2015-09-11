package alt.flex.client.api.operation;

import com.google.protobuf.ByteString;

import alt.flex.client.api.Nothing;


/**
 * 
 * @author Albert Shift
 *
 */

public interface PutAllOperation extends AbstractOperation<PutAllOperation, Nothing> {

	PutAllOperation add(String key, ByteString value);
	
}
