package net.geforcemods.securitycraft.platform.network;

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
}
