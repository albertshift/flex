package alt.flex.server;

import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;

/**
 * 
 * @author Albert Shift
 *
 */

public class SleepingOneMillisecondStrategy implements WaitStrategy {

	private final long startingTime;
	
	public SleepingOneMillisecondStrategy() {
		this.startingTime = System.currentTimeMillis();
	}
	
	public SleepingOneMillisecondStrategy(long startMls) {
		this.startingTime = startMls;
	}
	
	@Override
	public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier)
			throws AlertException, InterruptedException, TimeoutException {

		System.out.println("sequence=" + sequence + ", cursor=" + cursor.get() + ", dependentSequence=" + dependentSequence.get() + ", tid = " + Thread.currentThread().getId());
		
		while((System.currentTimeMillis() - startingTime)/1000 < sequence) {
			
			Thread.sleep(1000);
			
		}
		
		return sequence;
	}

	@Override
	public void signalAllWhenBlocking() {
	}

}
