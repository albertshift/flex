package alt.flex.support.timewindow;



/**
 * 
 * @author Albert Shift
 *
 */

public class TimerTaskEntryCollector<E extends Collectable> extends AbstractEntryCollector<E> {

	public TimerTaskEntryCollector(CollectableTimebasedWindow<E> timebasedWindow) {
		super(timebasedWindow);
	}
	
	@Override
	public void run() {

		doCollect();
		
	}
	
}
