package alt.flex.support.timewindow;


/**
 * 
 * @author Albert Shift
 *
 */

public interface CollectableTimebasedWindow<E> {

	E getEntryNoCheck(long sequence);
	
	CollectableSequencer getCollectableSequencer();
	
}
