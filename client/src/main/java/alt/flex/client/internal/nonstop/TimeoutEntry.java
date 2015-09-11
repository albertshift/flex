package alt.flex.client.internal.nonstop;

import java.util.Collection;

import alt.flex.client.internal.FlexFuture;
import alt.flex.support.timewindow.Collectable;


/**
 * 
 * @author Albert Shift
 *
 */

public interface TimeoutEntry extends Collectable {

	int add(FlexFuture future);

	FlexFuture remove(int requestNum);
	
	void fireTimeouts();
	
	void clear();
	
	Collection<FlexFuture> getFutures();
}
