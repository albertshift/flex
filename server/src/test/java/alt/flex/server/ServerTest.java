package alt.flex.server;

import org.junit.Test;

import alt.flex.server.FlexServer;
import alt.flex.server.SimpleHeapStore;

/**
 * 
 * @author Albert Shift
 *
 */

public class ServerTest {

	@Test
	public void test() throws Exception {
		
		FlexServer server = FlexServer.newBuilder().setPort(3333).setHeartBreathIntervalMls(5000).build();
		
		server.addStore("test", new SimpleHeapStore());
		
		server.start();
		
		server.join();
		
	}
	
}
