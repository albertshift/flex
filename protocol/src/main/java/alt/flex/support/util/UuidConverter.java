package alt.flex.support.util;

import java.util.UUID;

import alt.flex.protocol.FlexProtocol;

/**
 * 
 * @author Albert Shift
 *
 */

public final class UuidConverter {

	private UuidConverter() {
	}
	
	public static UUID toJavaUuid(FlexProtocol.UUID uuid) {
		return new UUID(uuid.getMostSigBits(), uuid.getLeastSigBits());
	}
	
	public static FlexProtocol.UUID toFlexUuid(UUID uuid) {
		FlexProtocol.UUID.Builder b = FlexProtocol.UUID.newBuilder();
		b.setMostSigBits(uuid.getMostSignificantBits());
		b.setLeastSigBits(uuid.getLeastSignificantBits());
		return b.build();
	}
	
}
