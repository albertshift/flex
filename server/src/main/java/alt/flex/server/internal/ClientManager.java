package alt.flex.server.internal;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.concurrent.Executor;

import alt.flex.protocol.FlexProtocol.InitParams;
import alt.flex.protocol.FlexProtocol.Request;

/**
 * 
 * @author Albert Shift
 *
 */

public interface ClientManager {

	void start(Executor executor);
	
	void registerNewClient(Channel channel, InitParams init);
	
	void unregisterClient(Channel channel);
	
	Iterator<Channel> getConnectedClients();
	
	ClientInformation findActiveClient(Channel channel);
	
	RequestCanceler findRequestCanceler(Channel channel, Request request);
	
}
