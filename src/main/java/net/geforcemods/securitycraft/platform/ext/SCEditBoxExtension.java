package net.geforcemods.securitycraft.platform.ext;

import java.util.function.Predicate;

/**
 * Recreates the text filter of edit boxes that vanilla removed in 26.1 and NeoForge adds back. Injected into EditBox and
 * implemented by EditBoxMixin.
 */
public interface SCEditBoxExtension {
	default void setFilter(Predicate<String> filter) {
		throw new AssertionError("Implemented by mixin");
	}
}
