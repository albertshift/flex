package alt.flex.support.timewindow;

import java.util.Timer;
import java.util.concurrent.Executor;

/**
 * 
 * @author Albert Shift
 *
 */

public interface TimeWindow<E> {

	void start(Executor executor);

	void start(Timer timer);

	long getStartingTimeMls();
	
	long getSequenceForTimePoint(long overStartingTimeMls, long overPointTimeMls);
	
	long getSequenceFor(int timeoutMls);
	
	E getEntry(long sequence);
	
}
