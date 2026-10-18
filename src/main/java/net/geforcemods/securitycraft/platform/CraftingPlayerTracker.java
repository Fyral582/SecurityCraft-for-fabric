package net.geforcemods.securitycraft.platform;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;

/**
 * Replacement for NeoForge's CommonHooks#getCraftingPlayer. The player is set while a crafting result is taken out of a
 * crafting grid, see ResultSlotMixin.
 */
public final class CraftingPlayerTracker {
	private static final ThreadLocal<Player> CRAFTING_PLAYER = new ThreadLocal<>();

	private CraftingPlayerTracker() {}

	@Nullable
	public static Player getCraftingPlayer() {
		return CRAFTING_PLAYER.get();
	}

	public static void setCraftingPlayer(@Nullable Player player) {
		if (player == null)
			CRAFTING_PLAYER.remove();
		else
			CRAFTING_PLAYER.set(player);
	}
}
