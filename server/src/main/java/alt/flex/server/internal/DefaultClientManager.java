package alt.flex.server.internal;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import alt.flex.protocol.FlexProtocol.InitParams;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.server.FlexServerBuilder;
import alt.flex.server.internal.cancel.CancelEntry;
import alt.flex.server.internal.cancel.ConcurrentHashSetCancelEntry;
import alt.flex.support.timewindow.EntryFactory;
import alt.flex.support.timewindow.TimeWindow;
import alt.flex.support.timewindow.TimebasedRingBuffer;
import alt.flex.support.util.UuidConverter;

/**
 * 
 * @author Albert Shift
 *
 */

public final class DefaultClientManager implements ClientManager {

	private final ConcurrentMap<UUID, ClientInformation> registeredClients = new ConcurrentHashMap<UUID, ClientInformation>();
	private final ConcurrentMap<Channel, ClientInformation> connectedClients = new ConcurrentHashMap<Channel, ClientInformation>();

	private final TimeWindow<CancelEntry> cancelWindow;
	
	public DefaultClientManager(FlexServerBuilder settings) {
		
		this.cancelWindow = new TimebasedRingBuffer<CancelEntry>(settings.getCancelWindowSec(), 1, TimeUnit.SECONDS, new EntryFactory<CancelEntry>() {

			@Override
			public CancelEntry newInstance() {
				return new ConcurrentHashSetCancelEntry();
			}
			
		});
		
	}
	
	@Override
	public void start(Executor executor) {
		
		this.cancelWindow.start(executor);
		
	}
	
	@Override
	public void registerNewClient(Channel channel, InitParams init) {
		
		if (!init.hasInitSeq()) {
			throw new IllegalArgumentException("init seq was not found");
		}
		
		UUID clientId = UuidConverter.toJavaUuid(init.getClientId());
		
		ClientInformation info = registeredClients.get(clientId);
		
		boolean newClient = false;
		while (info == null) {
			info = new ClientInformation(clientId, init.getInitSeq(), channel);
			
			if (null == registeredClients.putIfAbsent(clientId, info)) {
				newClient = true;
				break;
			}
			
			info = registeredClients.get(clientId);
		}
		
		if (!newClient) {
			info.setActive(channel);
		}
	
		connectedClients.put(channel, info);
		
	}

	@Override
	public void unregisterClient(Channel channel) {
		
		ClientInformation info = connectedClients.remove(channel);
		
		if (info != null) {
			info.setInactive();
		}
		
	}

	@Override
	public Iterator<Channel> getConnectedClients() {
		return connectedClients.keySet().iterator();
	}

	@Override
	public ClientInformation findActiveClient(Channel channel) {
		return connectedClients.get(channel);
	}

	public CancelEntry getCancelEntry(long initServerTimeMls, long clientTimeoutMlsSinceInit) {

		long serverSeq = cancelWindow.getSequenceForTimePoint(initServerTimeMls, clientTimeoutMlsSinceInit);
		
		return cancelWindow.getEntry(serverSeq);
	}

	@Override
	public RequestCanceler findRequestCanceler(Channel channel, Request request) {

		ClientInformation info = findActiveClient(channel);
		if (info == null) {
			return null;
		}

		return new DefaultRequestCanceler(this, info, request);
	}
	
	
}
