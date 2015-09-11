package alt.flex.support.timewindow;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 
 * @author Albert Shift
 *
 */

public final class TimebasedRingBuffer<E extends Collectable> extends CacheLinePad implements TimeWindow<E>, CollectableTimebasedWindow<E> {

	private final int size;
	private final long mask;
	
	private final AtomicReferenceArray<E> ringBuffer;
	
	private final IntervalSequencer sequencer;
	
	private final AtomicBoolean started = new AtomicBoolean(false);

	public TimebasedRingBuffer(int ringBufferSize, int timeValue, TimeUnit timeUnit, EntryFactory<E> factory) {
		
		if (ringBufferSize <= 0) {
			throw new IllegalArgumentException("ringBufferSize less or equal zero");
		}
		
    if ((ringBufferSize & (ringBufferSize - 1)) != 0) {
      throw new IllegalArgumentException("ringBufferSize not a power of two");
    }
		
		this.size = ringBufferSize;
		this.mask = ringBufferSize - 1;
		
		this.ringBuffer = new AtomicReferenceArray<E>(ringBufferSize);
		
		for (int i = 0; i != this.size; ++i) {
			this.ringBuffer.set(i, factory.newInstance());
		}
		
		this.sequencer = new IntervalSequencer(size, timeValue, timeUnit);

	}
	
	@Override
	public void start(Executor executor) {
		
		if (started.compareAndSet(false, true)) {
			executor.execute(new SleepingEntryCollector<E>(this));
		}

	}
	
	@Override
	public void start(Timer timer) {
		
		int timeInterval = sequencer.getTimeInterval();
		TimeUnit timeUnit = sequencer.getTimeUnit();
		
		if (timeUnit == TimeUnit.MICROSECONDS || timeUnit == TimeUnit.NANOSECONDS) {
			throw new IllegalArgumentException("use executor for this timeUnit " + timeUnit);
		}
		
		if (started.compareAndSet(false, true)) {
			
			long milliseconds = timeUnit.toMillis(timeInterval);
			timer.scheduleAtFixedRate(new TimerTaskEntryCollector<E>(this), new Date(), milliseconds);
			
		}

	}
	
	@Override
	public E getEntry(long sequence) {
		
		if (sequence < sequencer.getMinUncollectedSequence()) {
			return null;
		}
		
		if (sequence > sequencer.getMaxUncollectedSequence()) {
			return null;
		}
		
		return getEntryNoCheck(sequence);
	}
	
	@Override
	public E getEntryNoCheck(long sequence) {
		
		long index = (sequence & mask);
		
		return ringBuffer.get((int) index);
	}

	@Override
	public long getStartingTimeMls() {
		return sequencer.getStartingTimeMls();
	}
	
	@Override
	public long getSequenceFor(int timeoutMls) {
		return sequencer.getSequenceFor(timeoutMls);
	}

	@Override
	public CollectableSequencer getCollectableSequencer() {
		return sequencer;
	}

	@Override
	public long getSequenceForTimePoint(long overStartingTimeMls, long overPointTimeMls) {
		return sequencer.mapSequence(overStartingTimeMls, overPointTimeMls);
	}

}
