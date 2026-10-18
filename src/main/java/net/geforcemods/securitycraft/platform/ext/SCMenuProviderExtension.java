package net.geforcemods.securitycraft.platform.ext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Recreates NeoForge's IMenuProviderExtension. Injected into {@link net.minecraft.world.MenuProvider}.
 */
public interface SCMenuProviderExtension {
	/**
	 * Writes additional data that is sent to the client when the menu is opened
	 */
	default void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {}
}
