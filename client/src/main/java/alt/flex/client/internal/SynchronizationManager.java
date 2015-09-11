package alt.flex.client.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;

import alt.flex.client.FlexClientBuilder;
import alt.flex.client.internal.nonstop.ConcurrentSkipListTimeoutEntry;
import alt.flex.client.internal.nonstop.TimeoutEntry;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.support.timewindow.EntryFactory;
import alt.flex.support.timewindow.TimeWindow;
import alt.flex.support.timewindow.TimebasedRingBuffer;

/**
 * 
 * @author Albert Shift
 *
 */

public final class SynchronizationManager {

	private final TimeWindow<TimeoutEntry> timeoutWindow;
	
	public SynchronizationManager(FlexClientBuilder settings) {
		
		this.timeoutWindow = new TimebasedRingBuffer<TimeoutEntry>(settings.getTimeoutWindowMls(), 1, TimeUnit.MILLISECONDS, new EntryFactory<TimeoutEntry>() {

			@Override
			public TimeoutEntry newInstance() {
				return new ConcurrentSkipListTimeoutEntry();
			}
			
		});
		
	}
	
	public void start(Executor executor) {
		
		this.timeoutWindow.start(executor);
		
	}
	
	public TimeoutEntry getTimeoutEntry(long timeoutSeq) {
		return timeoutWindow.getEntry(timeoutSeq);
	}
	
	public ListenableFuture<Response> registerRequest(Request.Builder requestBuilder, int timeoutMls, CancellableAndSendable operation) {
		
		long timeoutSeq = 0;
		TimeoutEntry entry = null;
		
		while(entry == null) {
			timeoutSeq = timeoutWindow.getSequenceFor(timeoutMls);
			entry = timeoutWindow.getEntry(timeoutSeq);
		}
		
		FlexFuture future = FlexFuture.create(operation);
		int requestNum = entry.add(future);
		
		requestBuilder.setTimeoutSeq(timeoutSeq);
		requestBuilder.setRequestNum(requestNum);

		return future;
	}
	
	public void fireResult(Response response) {
		
		long timeoutSeq = response.getTimeoutSeq(); 
		int requestNum = response.getRequestNum();
		
		TimeoutEntry entry = timeoutWindow.getEntry(timeoutSeq);
		if (entry != null) {
			
			FlexFuture future = entry.remove(requestNum);
			future.set(response);
			
		}
		
	}
	
	public long getCurrentSeq() {
		return timeoutWindow.getSequenceFor(0);
	}
	
}
