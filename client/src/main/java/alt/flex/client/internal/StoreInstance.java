package alt.flex.client.internal;

import com.google.protobuf.ByteString;

import alt.flex.client.api.FlexStore;
import alt.flex.client.api.operation.ClearOperation;
import alt.flex.client.api.operation.GetAllOperation;
import alt.flex.client.api.operation.GetOperation;
import alt.flex.client.api.operation.PutAllOperation;
import alt.flex.client.api.operation.PutOperation;
import alt.flex.client.api.operation.SizeOperation;
import alt.flex.client.internal.op.ClearOp;
import alt.flex.client.internal.op.GetAllOp;
import alt.flex.client.internal.op.GetOp;
import alt.flex.client.internal.op.PutAllOp;
import alt.flex.client.internal.op.PutOp;
import alt.flex.client.internal.op.SizeOp;
import alt.flex.protocol.FlexProtocol.StoreInformation;

/**
 * 
 * @author Albert Shift
 *
 */

public class StoreInstance implements FlexStore {

	private final ClientInstance clientInstance;
	private final int storeId;
	private final String storeName;
	
	public StoreInstance(ClientInstance clientInstance, StoreInformation store) {
		this.clientInstance = clientInstance;
		this.storeId = store.getStoreId();
		this.storeName = store.getName();
	}

	@Override
	public int getStoreId() {
		return storeId;
	}
	
	@Override
	public String getStoreName() {
		return storeName;
	}

	@Override
	public GetOperation get(String key) {
		return new GetOp(clientInstance, storeId, key);
	}

	@Override
	public GetAllOperation getAll() {
		return new GetAllOp(clientInstance, storeId);
	}

	@Override
	public PutOperation put(String key, ByteString value) {
		return new PutOp(clientInstance, storeId, key, value);
	}

	@Override
	public PutAllOperation putAll() {
		return new PutAllOp(clientInstance, storeId);
	}

	@Override
	public SizeOperation size() {
		return new SizeOp(clientInstance, storeId);
	}

	@Override
	public ClearOperation clear() {
		return new ClearOp(clientInstance, storeId);
	}
	
}
