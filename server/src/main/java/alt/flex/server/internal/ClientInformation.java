package alt.flex.server.internal;

import io.netty.channel.Channel;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 * @author Albert Shift
 *
 */

public final class ClientInformation {

	private final UUID clientId;
	private final long initClientSeq;
	private final long initServerTimeMls;
	
	private volatile ClientStatus status;
	
	private final AtomicReference<Channel> currentChannel;
		
	public ClientInformation(UUID clientId, long initClientSeq, Channel ch) {
		this.clientId = clientId;
		this.initClientSeq = initClientSeq;
		this.initServerTimeMls = System.currentTimeMillis();
		this.status = ClientStatus.ACTIVE;
		this.currentChannel = new AtomicReference<Channel>(ch);
	}

	public UUID getClientId() {
		return clientId;
	}
	
	public ClientStatus getStatus() {
		return status;
	}

	public void setInactive() {
		status = ClientStatus.INACTIVE;
		currentChannel.set(null);
	}
	
	public void setActive(Channel channel) {
		status = ClientStatus.ACTIVE;
		currentChannel.set(channel);
	}
	
	public Channel getChannel() {
		return currentChannel.get();
	}

	public long getClientTimeoutMlsSinceInit(long timeoutSeq) {
		long seqDiff = timeoutSeq - initClientSeq;
		return seqDiff * ServerConstants.DEFAULT_CLIENT_MILLIS_PER_SEQ;
	}
	
	public long getInitServerTimeMls() {
		return initServerTimeMls;
	}

}
