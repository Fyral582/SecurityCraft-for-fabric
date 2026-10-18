package net.geforcemods.securitycraft.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Replacement for the parts of NeoForgeStreamCodecs that SecurityCraft uses
 */
public final class ExtraStreamCodecs {
	private ExtraStreamCodecs() {}

	public static <E extends Enum<E>> StreamCodec<FriendlyByteBuf, E> enumCodec(Class<E> enumClass) {
		return StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(enumClass));
	}
}
