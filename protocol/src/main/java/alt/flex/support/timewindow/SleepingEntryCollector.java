package alt.flex.support.timewindow;


/**
 * 
 * @author Albert Shift
 *
 */

public final class SleepingEntryCollector<E extends Collectable> extends AbstractEntryCollector<E> {
	
	public SleepingEntryCollector(CollectableTimebasedWindow<E> timebasedWindow) {
		super(timebasedWindow);
	}
	
	@Override
	public void run() {

		while(!Thread.interrupted()) {

			doCollect();
			
			try {
				sequencer.waitForNext();
			}
			catch(InterruptedException e) {
				break;
			}
			
		}
		
	}

}
