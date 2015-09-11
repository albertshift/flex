package alt.flex.server.internal;

import alt.flex.server.api.AbstractFlexStore;
import alt.flex.server.internal.util.MutableArrayList;

/**
 * 
 * @author Albert Shift
 *
 */

public class StoreReferenceManager {

	private final MutableArrayList<AbstractFlexStore> list = new MutableArrayList<AbstractFlexStore>(128);
	
	public AbstractFlexStore getStore(int storeId) {
		return list.get(storeId - ServerConstants.START_STORE_ID);
	}
	
	public void setStore(int storeId, AbstractFlexStore store) {
		list.set(storeId - ServerConstants.START_STORE_ID, store);
	}

	
}
