package net.geforcemods.securitycraft.platform.menu;

import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Replacement for NeoForge's IPlayerExtension#openMenu overloads that take additional data
 */
public final class MenuOpening {
	private MenuOpening() {}

	public static OptionalInt openMenu(Player player, MenuProvider provider, BlockPos pos) {
		return openMenu(player, provider, buf -> buf.writeBlockPos(pos));
	}

	public static OptionalInt openMenu(Player player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
		return player.openMenu(new MenuProvider() {
			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
				return provider.createMenu(windowId, inv, player);
			}

			@Override
			public Component getDisplayName() {
				return provider.getDisplayName();
			}

			@Override
			public boolean shouldCloseCurrentScreen() {
				return provider.shouldCloseCurrentScreen();
			}

			@Override
			public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
				extraDataWriter.accept(buffer);
			}
		});
	}
}
