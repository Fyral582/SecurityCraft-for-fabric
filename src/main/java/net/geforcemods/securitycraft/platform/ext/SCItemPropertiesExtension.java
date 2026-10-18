package net.geforcemods.securitycraft.platform.ext;

import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

/**
 * Allows using registry suppliers with {@link Item.Properties#component}, like NeoForge does. Injected into Item.Properties.
 */
public interface SCItemPropertiesExtension {
	default <T> Item.Properties component(Supplier<? extends DataComponentType<T>> type, T value) {
		return ((Item.Properties) this).component(type.get(), value);
	}
}
