package alt.flex.server.internal.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 
 * @author Albert Shift
 *
 * @param <T>
 */

public class MutableArrayList<T> {

	private static final int DEFAULT_INIT_CAPACITY = 16;
	
	private final int initCapacity;
	private final List<AtomicReferenceArray<T>> list = new CopyOnWriteArrayList<AtomicReferenceArray<T>>();
	
	public MutableArrayList() {
		this(DEFAULT_INIT_CAPACITY);
	}
	
	public MutableArrayList(int initCapacity) {
		this.initCapacity = initCapacity;
	}

	public T get(int i) {
		if (i < 0) {
			throw new IndexOutOfBoundsException("invalid index " + i);
		}
		int listNum = i / initCapacity;
		AtomicReferenceArray<T> array = list.get(listNum);
		
		return array.get(i % initCapacity);
		
	}
	
	public void set(int i, T newValue) {
		
		while (i >= list.size() * initCapacity) {
			list.add(new AtomicReferenceArray<T>(initCapacity));
		}
		
		int listNum = i / initCapacity;
		AtomicReferenceArray<T> array = list.get(listNum);
		
		array.set(i % initCapacity, newValue);
	}
	
}
