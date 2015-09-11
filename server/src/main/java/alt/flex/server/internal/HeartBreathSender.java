package alt.flex.server.internal;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.TimerTask;

import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseType;

/**
 * 
 * @author Albert Shift
 *
 */

public final class HeartBreathSender extends TimerTask {

	private final static Response HEART_BREATH_RESPONSE = Response.newBuilder().setResponseType(ResponseType.HEART_BREATH).build();

	private final ClientManager clientManager;
	
	public HeartBreathSender(ClientManager clientManager) {
		this.clientManager = clientManager;
	}
	
	@Override
	public void run() {
		
		Iterator<Channel> iterator = clientManager.getConnectedClients();
		
		while(iterator.hasNext()) {
			
			Channel channel = iterator.next();
			
			if (channel.isActive()) {
				channel.writeAndFlush(HEART_BREATH_RESPONSE);
			}
			
		}
		
	}
}
