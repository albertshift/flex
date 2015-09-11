package alt.flex.server;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 
 * @author Albert Shift
 *
 */

public class DisruptorTest {

	RingBuffer<ValueEvent> ringBuffer;
	
	@Test
	public void test() throws Exception {
		
		final int maxTimeout = 16;
		final long startMls = System.currentTimeMillis();
		
    ExecutorService exec = Executors.newCachedThreadPool();

    Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, maxTimeout, exec, ProducerType.MULTI, new SleepingOneMillisecondStrategy(startMls));
    final EventHandler<ValueEvent> handler = new EventHandler<ValueEvent>() {
        // event will eventually be recycled by the Disruptor after it wraps
        public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {

        	  System.out.println("Consume "  + event.getValue());
        	  
        	  event.clear();
            
            long last = (System.currentTimeMillis() - startMls)/1000 +  maxTimeout - 1;
            
            System.out.println("LAST = " + last + ", tid = " + Thread.currentThread().getId());
            
            while(ringBuffer.getCursor() < last) {
            	long seq = ringBuffer.next();
            	System.out.println("publish = " + seq);
            	ringBuffer.publish(seq);
            	
            }
        	
        }
    };
    // Build dependency graph
    disruptor.handleEventsWith(handler);
    ringBuffer = disruptor.start();

    ringBuffer.publish(ringBuffer.next());
    
    
    while(ringBuffer.getBufferSize() > 0) {
    	
    	ValueEvent v = ringBuffer.get((System.currentTimeMillis() - startMls)/1000 + 50);
    	
    	v.setValue(UUID.randomUUID().toString());
    	
    	System.out.println("Main Thread = " + Thread.currentThread().getId());
    	
    	Thread.sleep(1000);
    	
    }
    
    
    System.out.println("Done");
    

    disruptor.shutdown();
    exec.shutdown();
		
	}
	
}
