package net.geforcemods.securitycraft.platform.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Fabric replacement for NeoForge's IMenuTypeExtension#create
 */
public final class IMenuTypeExtension {
	private IMenuTypeExtension() {}

	public static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
		return new ExtendedMenuType<>(factory);
	}

	@FunctionalInterface
	public interface IContainerFactory<T extends AbstractContainerMenu> {
		T create(int windowId, Inventory inv, RegistryFriendlyByteBuf data);
	}
}
