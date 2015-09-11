package alt.flex.server.internal;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import alt.flex.protocol.ErrorCodes;
import alt.flex.protocol.FlexProtocol.ErrorInformation;
import alt.flex.protocol.FlexProtocol.KeyValuePair;
import alt.flex.protocol.FlexProtocol.Operation;
import alt.flex.protocol.FlexProtocol.OperationResult;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseTrace;
import alt.flex.protocol.FlexProtocol.ResponseType;
import alt.flex.protocol.FlexProtocol.ReturnType;
import alt.flex.protocol.FlexProtocol.ValueOrNull;
import alt.flex.server.api.AbstractFlexStore;
import alt.flex.server.api.SyncAtomicOperations;
import alt.flex.server.api.SyncBulkOperations;

import com.google.protobuf.ByteString;

/**
 * 
 * @author Albert Shift
 *
 */

public class SimpleRequestHandler implements Runnable {

	private final ChannelHandlerContext ctx; 
	private final Request request;
	private final StoreReferenceManager storeReferenceManager;
	private final ClientManager clientManager;
	private final boolean enableTrace;
	private final long beginWaitNanos;
	
	public SimpleRequestHandler(ChannelHandlerContext ctx, Request request, StoreReferenceManager storeReferenceManager, ClientManager clientManager) {
		this.ctx = ctx;
		this.request = request;
		this.storeReferenceManager = storeReferenceManager;
		this.clientManager = clientManager;
		
		this.enableTrace = request.hasEnableTrace() && request.getEnableTrace();
		this.beginWaitNanos = enableTrace ? System.nanoTime() : 0;
	}
	
	@Override
	public void run() {

		RequestCanceler requestCanceler = clientManager.findRequestCanceler(ctx.channel(), request);

		if (requestCanceler == null) {
			return;
		}
		
		if (requestCanceler.isCanceledOrTimeout()) {
			return;
		}
		
		long waitNanos = enableTrace ? System.nanoTime() - beginWaitNanos : 0;
		ResponseTrace.Builder trace = enableTrace ? ResponseTrace.newBuilder() : null;
		
		Response.Builder res = Response.newBuilder();
		
		res.setTimeoutSeq(request.getTimeoutSeq());
		res.setRequestNum(request.getRequestNum());
		res.setResponseType(ResponseType.OPERATION_RESULT);
		
		//if (request.getRequestId() % 2 == 0) {
		//	return;
		//}
		
		int count = request.getOperationCount();
		
		for (int i = 0; i != count; ++i) {
			
			if (requestCanceler.isCanceledOrTimeout()) {
				return;
			}
			
			Operation operation = request.getOperation(i);
			AbstractFlexStore store = storeReferenceManager.getStore(operation.getStoreId());
			
			if (store == null) {
				
				returnError(res, ErrorCodes.STORE_NOT_FOUND);
				
			}
			else {
				
				long beginOperationNanos = 0;
				if (enableTrace) {
					beginOperationNanos = System.nanoTime();
				}
				
				handleOperation(operation, store, res);
				
				if (enableTrace) {
					trace.addOperationExecNanos(System.nanoTime() - beginOperationNanos);
				}
				
			}
			
		}

		if (enableTrace) {
			
			trace.setWaitNanos(waitNanos);
			
			res.setTrace(trace.build());
		}
		
		if (requestCanceler.isCanceledOrTimeout()) {
			return;
		}
		
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.writeAndFlush(res.build());
		}
		
	}
	
	private void handleOperation(Operation operation, AbstractFlexStore store, Response.Builder res) {
		
		int count = operation.getKeyValueCount();
		
		switch(operation.getOperationType()) {
		
		case GET:
			if (count == 1) {
				KeyValuePair kv = operation.getKeyValue(0);
				handleGetOperation(kv, store, res);
			}
			else {
				handleGetAllOperation(operation.getKeyValueList(), store, res);
			}
			break;
			
		case PUT:
			if (count == 1) {
				KeyValuePair kv = operation.getKeyValue(0);
				handlePutOperation(kv, store, res);
			}
			else {
				handlePutAllOperation(operation.getKeyValueList(), store, res);
			}
			break;
			
		case REMOVE:
			if (count == 1) {
				KeyValuePair kv = operation.getKeyValue(0);
				handleRemoveOperation(kv, store, res);
			}
			else {
				handleRemoveAllOperation(operation.getKeyValueList(), store, res);
			}			
			break;
			
		case CLEAR:
			handleClearOperation(store, res);
			break;

		case SIZE:
			handleSizeOperation(store, res);
			break;

		default:
			returnError(res, ErrorCodes.STORE_UNKNOWN_OPERATION);
			break;
			
		}
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 *  Handle operation methods
	 *  
	 *   
	 *   
	 *   
	 *   
	 */
	
	private void handleGetOperation(KeyValuePair kv, AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			ByteString value = syncStore.get(kv.getKey());
			
			ValueOrNull.Builder vb = ValueOrNull.newBuilder();
			if (value != null) {
				vb.setValue(value);
			}
			
			returnSingleValue(res, vb.build());
			
		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}

	private void handleGetAllOperation(List<KeyValuePair> keyValues, AbstractFlexStore store, Response.Builder res) {

		if (store instanceof SyncBulkOperations) {
			
			SyncBulkOperations bulkStore = (SyncBulkOperations) store;
			
			int size = keyValues.size();
			String[] keys = new String[size];
			
			for (int i = 0; i != size; ++i) {
				keys[i] = keyValues.get(i).getKey();
			}
			
			ByteString[] values = bulkStore.getAll(keys);
			
			OperationResult.Builder result = OperationResult.newBuilder();
			result.setReturnType(ReturnType.MULTIPLE_VALUES);

			for (ByteString value : values) {
				
				ValueOrNull.Builder vb = ValueOrNull.newBuilder();
				if (value != null) {
					vb.setValue(value);
				}
				
				result.addValue(vb.build());
				
			}

			res.addResult(result.build());
			
		}
		else if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			OperationResult.Builder result = OperationResult.newBuilder();
			result.setReturnType(ReturnType.MULTIPLE_VALUES);

			for (KeyValuePair kv : keyValues) {
				
				ByteString value = syncStore.get(kv.getKey());
				
				ValueOrNull.Builder vb = ValueOrNull.newBuilder();
				if (value != null) {
					vb.setValue(value);
				}
				
				result.addValue(vb.build());
				
			}

			res.addResult(result.build());
			
		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}

	private void handlePutOperation(KeyValuePair kv, AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			syncStore.put(kv.getKey(), kv.getValue());
			
			returnSuccessNothing(res);
			
		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
				
	}

	private void handlePutAllOperation(List<KeyValuePair> keyValues, AbstractFlexStore store, Response.Builder res) {

		if (store instanceof SyncBulkOperations) {
			
			SyncBulkOperations bulkStore = (SyncBulkOperations) store;
			
			int size = keyValues.size();
			String[] keys = new String[size];
			ByteString[] values = new ByteString[size];
			
			for (int i = 0; i != size; ++i) {
				KeyValuePair kv = keyValues.get(i);
				keys[i] = kv.getKey();
				values[i] = kv.getValue();
			}
			
			bulkStore.putAll(keys, values);
			
			returnSuccessNothing(res);
			
		}
		else if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			for (KeyValuePair kv : keyValues) {
				syncStore.put(kv.getKey(), kv.getValue());
			}
			
			returnSuccessNothing(res);
			
		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}
	
	private void handleRemoveOperation(KeyValuePair kv, AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncAtomicOperations) {

			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			syncStore.remove(kv.getKey());
			
			returnSuccessNothing(res);
			
		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}
	
	private void handleRemoveAllOperation(List<KeyValuePair> keyValues, AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncBulkOperations) {
			
			SyncBulkOperations bulkStore = (SyncBulkOperations) store;

			int size = keyValues.size();
			String[] keys = new String[size];
			
			for (int i = 0; i != size; ++i) {
				keys[i] = keyValues.get(i).getKey();
			}
			
			bulkStore.removeAll(keys);

			returnSuccessNothing(res);

		}
		else if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			for (KeyValuePair kv : keyValues) {
				syncStore.remove(kv.getKey());
			}
			
			returnSuccessNothing(res);

		}
		else {
			
			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}
	
	private void handleClearOperation(AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			syncStore.clear();
			
			returnSuccessNothing(res);
			
		}
		else {

			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}
	
	private void handleSizeOperation(AbstractFlexStore store, Response.Builder res) {
		
		if (store instanceof SyncAtomicOperations) {
			
			SyncAtomicOperations syncStore = (SyncAtomicOperations) store;
			
			long size = syncStore.size();
			
			returnSingleValue(res, ValueOrNull.newBuilder().setLongValue(size).build());
			
		}
		else {

			returnError(res, ErrorCodes.STORE_DNOT_SUPPORT_OPERATION);
			
		}
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 *  Support methods 
	 *  
	 *  
	 *  
	 *  
	 */
	
	private void returnError(Response.Builder res, int errorCode) {
		ErrorInformation err = ErrorInformation.newBuilder().setCode(errorCode).build();
		
		OperationResult result = OperationResult.newBuilder()
				.setReturnType(ReturnType.ERROR_OR_EXCEPTION).setError(err).build();
		res.addResult(result);
	}
	
	private void returnSuccessNothing(Response.Builder res) {
		
		OperationResult.Builder result = OperationResult.newBuilder();
		result.setReturnType(ReturnType.NOTHING);
		
		res.addResult(result.build());
		
	}
	
	private void returnSingleValue(Response.Builder res, ValueOrNull value) {
		
		OperationResult.Builder result = OperationResult.newBuilder();
		result.setReturnType(ReturnType.SINGLE_VALUE);
		
		result.addValue(value);
		
		res.addResult(result.build());
		
	}
}
