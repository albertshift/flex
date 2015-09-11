package alt.flex.client;

import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;

import alt.flex.client.FlexClient;
import alt.flex.client.api.FlexStore;
import alt.flex.client.api.LifecycleListener;
import alt.flex.client.api.RequestTrace;
import alt.flex.client.api.TraceLogger;

/**
 * 
 * @author Albert Shift
 *
 */

public class ClientTest implements LifecycleListener, TraceLogger {

	@Override
	public void log(RequestTrace trace) {
		System.out.println(trace);
	}

	@Override
	public void clientActive() {
		System.out.println("client active");
	}

	@Override
	public void clientInactive() {
		System.out.println("client inactive");
	}

	@Override
	public void onConnectionException(Exception e) {
		e.printStackTrace();
	}

	@Test
	public void test() throws Exception {
		
		FlexClient client = FlexClient.newBuilder().setHost("localhost").setPort(3333).setLifecycleListener(this).build();
		
		client.start();

		while(!client.isActive()) {
			Thread.sleep(1000);
		}

		System.out.println((client.isActive() ? "connected" : "disconnected") + ", available stores=" + client.getStoreNames());

		FlexStore store = client.getStore("test");
		
		store.put("key", ByteString.copyFrom("bytes".getBytes())).sync();
		
		for (int i = 0; i != 10; ++i) {
		
			try {
			
				ByteString result = store.get("key").enableTrace(this).sync();
			
				System.out.println("result = " + result.toStringUtf8());
			
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		

		final ListenableFuture<ByteString> future = store.get("key").async();
		future.addListener(new Runnable() {

			@Override
			public void run() {
				
				try {
					
					if (future.isCancelled()) {
						System.out.println("reauest was canceled = " + future);
					}
					else if (future.isDone()) {
						ByteString val = future.get();
						System.out.println("async result = " + val.toStringUtf8());
					}

				} catch (Exception e) {
					System.out.println("!!! Cancel " + e.getCause());
				}
				
			}
			
		}, client.getDefaultExecutor());
		
		
		future.cancel(true);
		
		

		while(true) {
			Thread.sleep(1000);
			
			final ListenableFuture<Long> f = store.size().async();
			
			f.addListener(new Runnable() {

				@Override
				public void run() {
					if (f.isDone()) {
						try {
							System.out.println("size = " + f.get());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}, client.getDefaultExecutor());
			
			
			
		}
		
}
	
}
