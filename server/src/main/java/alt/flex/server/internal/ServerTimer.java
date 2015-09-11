package alt.flex.server.internal;

import java.util.Timer;

import alt.flex.server.FlexServerBuilder;

/**
 * Singleton of the Timer
 * 
 * @author Albert Shift
 *
 */

public class ServerTimer {

	private final Timer timer = new Timer("flex-server-timer");

	public void schedule(HeartBreathSender sender, FlexServerBuilder settings) {

		timer.scheduleAtFixedRate(sender, settings.getHeartBreathIntervalMls(),
				settings.getHeartBreathIntervalMls());

	}
	
	public void shutdown() {
		timer.cancel();
	}

}
