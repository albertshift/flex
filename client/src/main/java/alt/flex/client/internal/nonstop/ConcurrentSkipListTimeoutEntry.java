package alt.flex.client.internal.nonstop;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import alt.flex.client.internal.FlexFuture;
import alt.flex.support.timewindow.CacheLinePad;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ConcurrentSkipListTimeoutEntry extends CacheLinePad implements TimeoutEntry {
	
	private final static int INIT_REQUEST_NUM = 1;
	
	private final AtomicInteger nextRequestNum = new AtomicInteger(INIT_REQUEST_NUM);
	
	private final ConcurrentSkipListMap<Integer, FlexFuture> m = new ConcurrentSkipListMap<Integer, FlexFuture>();
	
	@Override
	public int add(FlexFuture future) {
		
		int requestNum = nextRequestNum();
		
		//long nanos = System.nanoTime();
		
		boolean added = m.putIfAbsent(requestNum, future) == null;
		if (!added) {
			throw new IllegalStateException("can not register request " + requestNum);
		}
		
		//System.out.println("ConcurrentSkipListMap put=" + (System.nanoTime() - nanos));
		
		return requestNum;
	}

	@Override
	public FlexFuture remove(int requestNum) {
		//long nanos = System.nanoTime();
		FlexFuture future = m.remove(requestNum);
		//System.out.println("ConcurrentSkipListMap remove=" + (System.nanoTime() - nanos));
		return future;
	}
	
	@Override
	public Collection<FlexFuture> getFutures() {
		return m.values();
	}
	
	private int nextRequestNum() {
		
		int requestNum = nextRequestNum.getAndIncrement();
		
		if (requestNum < 0) {
			throw new IllegalStateException("nextRequestNum is out of bound");
		}
		
		return requestNum;
	}
	
	
	@Override
	public void fireTimeouts() {
		for (FlexFuture future : m.values()) {
			
			try {
				future.setTimeout();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void clear() {
		//long nanos = System.nanoTime();
		m.clear();
		//System.out.println("ConcurrentSkipListMap clear=" + (System.nanoTime() - nanos));
		
		nextRequestNum.set(INIT_REQUEST_NUM);
		
	}

	@Override
	public void collect() {

		fireTimeouts();
		
		clear();
	}

	
}
