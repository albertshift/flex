package alt.flex.server.internal;

/**
 * 
 * @author Albert Shift
 *
 */

public interface RequestCanceler {

	boolean isCanceledOrTimeout();
	
	void cancel();
	
}
