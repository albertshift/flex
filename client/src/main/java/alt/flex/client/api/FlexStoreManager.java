package alt.flex.client.api;

import java.util.Set;

import alt.flex.client.api.operation.BatchOperation;

/**
 * 
 * @author Albert Shift
 *
 */

public interface FlexStoreManager {

	FlexStore getStore(String name);

	Set<String> getStoreNames();
	
	BatchOperation batch();
	
}
