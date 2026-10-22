package net.geforcemods.securitycraft.platform.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.geforcemods.securitycraft.platform.network.IPayloadContext;
import net.minecraft.world.entity.player.Player;

/**
 * Fabric replacement for NeoForge's IPayloadContext. Payload handlers are already called on the main thread.
 */
@FunctionalInterface
public interface IPayloadContext {
	Player player();

	default void enqueueWork(Runnable runnable) {
		runnable.run();
	}

	/**
	 * Sends a payload back to the player that sent the payload that is currently being handled on the server
	 */
	default void reply(CustomPacketPayload payload) {
		if (player() instanceof ServerPlayer serverPlayer)
			ServerPlayNetworking.send(serverPlayer, payload);
	}
}
