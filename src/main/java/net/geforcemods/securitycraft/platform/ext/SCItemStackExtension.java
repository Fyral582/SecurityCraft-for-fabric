package net.geforcemods.securitycraft.platform.ext;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

/**
 * Allows using registry suppliers with {@link ItemStack}'s component setters, like NeoForge does. Injected into ItemStack.
 */
public interface SCItemStackExtension {
	private ItemStack self() {
		return (ItemStack) this;
	}

	default <T> T set(Supplier<? extends DataComponentType<T>> type, T value) {
		return self().set(type.get(), value);
	}

	default <T> T remove(Supplier<? extends DataComponentType<? extends T>> type) {
		return self().remove(type.get());
	}

	default <T> T update(Supplier<? extends DataComponentType<T>> type, T defaultValue, UnaryOperator<T> updater) {
		return self().update(type.get(), defaultValue, updater);
	}

	default void copyFrom(Supplier<? extends DataComponentType<?>> type, DataComponentGetter src) {
		self().copyFrom(type.get(), src);
	}
}
