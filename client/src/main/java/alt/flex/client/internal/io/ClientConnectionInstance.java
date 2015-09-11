package alt.flex.client.internal.io;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;

import alt.flex.client.FlexClientBuilder;
import alt.flex.client.support.FlexConnectionException;

/**
 * 
 * @author Albert Shift
 *
 */

public class ClientConnectionInstance {

	private final Channel channel;
	private final DefaultClientHandler handler;
	
	private final AtomicBoolean closed = new AtomicBoolean(false);

	public ClientConnectionInstance(Bootstrap bootstrap, FlexClientBuilder settings) {
		this.channel = connect(bootstrap, settings);
		this.handler = channel.pipeline().get(DefaultClientHandler.class);
	}

	
	protected Channel connect(Bootstrap bootstrap, FlexClientBuilder settings) {
		try {
			return bootstrap.connect(settings.getHost(), settings.getPort()).sync().channel();
		} catch (InterruptedException e) {
			throw new FlexConnectionException("fail to connect to " + settings.getHost() + ":"
					+ settings.getPort(), e);
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public DefaultClientHandler getHandler() {
		return handler;
	}

	public void close() {
		
		if (!closed.compareAndSet(false, true)) {
			return;
		}
		
		channel.close();
	}

}
