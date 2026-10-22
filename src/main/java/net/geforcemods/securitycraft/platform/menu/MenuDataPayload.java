package net.geforcemods.securitycraft.platform.menu;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Carries the additional data of an {@link ExtendedMenuType} menu to the client
 */
public record MenuDataPayload(int windowId, byte[] data) implements CustomPacketPayload {
	public static final Type<MenuDataPayload> TYPE = new Type<>(SecurityCraft.resLoc("menu_data"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, MenuDataPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, MenuDataPayload::windowId,
			ByteBufCodecs.BYTE_ARRAY, MenuDataPayload::data,
			MenuDataPayload::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle() {
		ExtendedMenuType.putPendingData(windowId, data);
	}
}
