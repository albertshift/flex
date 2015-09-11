package alt.flex.server.internal.cancel;

import java.util.UUID;

/**
 * 
 * Fully immutable client request id with the precalculated hashCode
 * 
 * @author Albert Shift
 *
 */

public final class ClientRequestId {

	private final UUID clientId;
	private final long timeoutSeq;
	private final int requestNum;
	
	private final int hashCode;
	
	private ClientRequestId(UUID clientId, long timeoutSeq, int requestNum) {
		
		this.clientId = clientId;
		this.timeoutSeq = timeoutSeq;
		this.requestNum = requestNum;
		
		this.hashCode = precalculateHashCode();
	}

	public static ClientRequestId create(UUID clientId, long timeoutSeq, int requestNum) {
		return new ClientRequestId(clientId, timeoutSeq, requestNum);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	private int precalculateHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + requestNum;
		result = prime * result + (int) (timeoutSeq ^ (timeoutSeq >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientRequestId other = (ClientRequestId) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (requestNum != other.requestNum)
			return false;
		if (timeoutSeq != other.timeoutSeq)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClientRequestId [clientId=" + clientId + ", timeoutSeq=" + timeoutSeq + ", requestNum="
				+ requestNum + "]";
	}


}
