package net.geforcemods.securitycraft.platform.ext;

import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;

/**
 * Allows using registry suppliers with {@link DataComponentHolder}, like NeoForge does. Injected into DataComponentHolder.
 */
public interface SCDataComponentHolderExtension {
	private DataComponentHolder self() {
		return (DataComponentHolder) this;
	}

	default <T> T get(Supplier<? extends DataComponentType<? extends T>> type) {
		return self().get(type.get());
	}

	default <T> T getOrDefault(Supplier<? extends DataComponentType<? extends T>> type, T defaultValue) {
		return self().getOrDefault(type.get(), defaultValue);
	}

	default boolean has(Supplier<? extends DataComponentType<?>> type) {
		return self().has(type.get());
	}
}
