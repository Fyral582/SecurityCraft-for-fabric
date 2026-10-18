package net.geforcemods.securitycraft.platform.menu;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.geforcemods.securitycraft.platform.menu.IMenuTypeExtension.IContainerFactory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * A menu type whose client side menu is created with additional data sent by the server. The data is sent by
 * {@link MenuDataPayload} right before the vanilla open screen packet, and is stored until the menu gets created.
 */
public class ExtendedMenuType<T extends AbstractContainerMenu> extends MenuType<T> {
	private static final Map<Integer, byte[]> PENDING_DATA = new HashMap<>();
	private final IContainerFactory<T> factory;

	public ExtendedMenuType(IContainerFactory<T> factory) {
		super((windowId, inv) -> null, FeatureFlags.VANILLA_SET);
		this.factory = factory;
	}

	@Override
	public T create(int windowId, Inventory inv) {
		byte[] data;

		synchronized (PENDING_DATA) {
			data = PENDING_DATA.remove(windowId);
		}

		RegistryAccess registryAccess = inv.player.level().registryAccess();
		RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data == null ? new byte[0] : data), registryAccess);

		try {
			return factory.create(windowId, inv, buf);
		}
		finally {
			buf.release();
		}
	}

	public T create(int windowId, Inventory inv, RegistryFriendlyByteBuf data) {
		return factory.create(windowId, inv, data);
	}

	public static void putPendingData(int windowId, byte[] data) {
		synchronized (PENDING_DATA) {
			PENDING_DATA.put(windowId, data);
		}
	}
}
