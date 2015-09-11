package alt.flex.support.timewindow;


/**
 * 
 * @author Albert Shift
 *
 */

public interface CollectableSequencer {

	boolean hasElements();
	
	long peek();
	
	void commit();
	
	void waitForNext() throws InterruptedException;
	
}
