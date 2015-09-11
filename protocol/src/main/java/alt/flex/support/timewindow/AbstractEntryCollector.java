package alt.flex.support.timewindow;

import java.util.TimerTask;

/**
 * 
 * @author Albert Shift
 *
 */

public abstract class AbstractEntryCollector<E extends Collectable> extends TimerTask {

	protected final CollectableTimebasedWindow<E> timebasedWindow;
	protected final CollectableSequencer sequencer;
	
	public AbstractEntryCollector(CollectableTimebasedWindow<E> timebasedWindow) {
		this.timebasedWindow = timebasedWindow;
		this.sequencer = timebasedWindow.getCollectableSequencer();
	}
	
	protected void doCollect() {
		
		while (sequencer.hasElements()) {
			
			long sequence = sequencer.peek();
			
			collectOneEntry(sequence);
			
			sequencer.commit();
		}
		
		
	}
	
	private void collectOneEntry(long sequence) {
		
		E entry = timebasedWindow.getEntryNoCheck(sequence);

		entry.collect();
		
	}
	
}
