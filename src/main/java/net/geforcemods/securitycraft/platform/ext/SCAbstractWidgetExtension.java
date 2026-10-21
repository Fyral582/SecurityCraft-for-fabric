package net.geforcemods.securitycraft.platform.ext;

/**
 * Recreates NeoForge's foreground color of widgets. Injected into AbstractWidget and implemented by AbstractWidgetMixin.
 */
public interface SCAbstractWidgetExtension {
	int UNSET_FG_COLOR = -1;

	default int getFGColor() {
		throw new AssertionError("Implemented by mixin");
	}

	default void setFGColor(int color) {
		throw new AssertionError("Implemented by mixin");
	}

	default void clearFGColor() {
		setFGColor(UNSET_FG_COLOR);
	}
}
