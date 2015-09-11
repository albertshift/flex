package alt.flex.support.timewindow;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;


/**
 * 
 * @author Albert Shift
 *
 */


public final class IntervalSequencer extends CacheLinePad implements CollectableSequencer {

	private final int timeInterval;
	private final TimeUnit timeUnit;
	
	private final long startingTimeMls;
	
	/*
	 * Everything that less than sequence is collected
	 */
	
	private final AtomicLong minUncollectedSequence;
	
	/*
	 * Everything that greater can not be used
	 */
	
	private final AtomicLong maxUncollectedSequence;
	
	public IntervalSequencer(int ringBufferSize, int timeInterval, TimeUnit timeUnit) {
		
		if (timeUnit == null) {
			throw new IllegalArgumentException("timeUnit is null");
		}
		
		this.timeInterval = timeInterval;
		this.timeUnit = timeUnit;
		this.startingTimeMls = System.currentTimeMillis();

		this.minUncollectedSequence = new AtomicLong(0L);
		this.maxUncollectedSequence = new AtomicLong(ringBufferSize-1);
	}
	
	public long getSequenceFor(int timeoutMls) {
		long value = minUncollectedSequence.get() + getIntervals(timeoutMls);
		return Math.min(value, maxUncollectedSequence.get());
	}
	
	public long mapSequence(long otherStartingTimeMls, long otherTimePointMls) {
		return getIntervals(otherStartingTimeMls - startingTimeMls + otherTimePointMls);
	}
	
	public long getMinUncollectedSequence() {
		return minUncollectedSequence.get();
	}

	public long getMaxUncollectedSequence() {
		return maxUncollectedSequence.get();
	}
	
	@Override
	public boolean hasElements() {
		return minUncollectedSequence.get() < getCurrentTimeSequence(); 
	}
	
	@Override
	public long peek() {
		return minUncollectedSequence.getAndIncrement();
	}
	
	@Override
	public void commit() {
		maxUncollectedSequence.incrementAndGet();
	}
	
	private long getCurrentTimeSequence() {
		long currentSequence = getIntervals(System.currentTimeMillis() - startingTimeMls);
		return currentSequence;
	}

	public long getStartingTimeMls() {
		return startingTimeMls;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	@Override
	public void waitForNext() throws InterruptedException {

		long milliseconds = timeUnit.toMillis(timeInterval);
		
		if (milliseconds > 0) {
			Thread.sleep(milliseconds);
		}
		else {
			long nanos = timeUnit.toNanos(timeInterval);
			LockSupport.parkNanos(nanos);
		}
		
	}

	private long getIntervals(long timeMls) {
		return timeUnit.convert(timeMls, TimeUnit.MILLISECONDS) / timeInterval;
	}
	
}
