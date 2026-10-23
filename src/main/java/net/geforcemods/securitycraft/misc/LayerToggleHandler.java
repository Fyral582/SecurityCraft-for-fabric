package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class LayerToggleHandler {
	private static final List<ResourceLocation> DISABLED_LAYERS = new ArrayList<>();

	private LayerToggleHandler() {}

	/**
	 * Fabric API does not have identified HUD layers in 1.21.2 and 1.21.3, so the vanilla HUD elements that SecurityCraft may
	 * disable are toggled via GuiMixin instead
	 */
	public static void init() {}

	/**
	 * Wraps a HUD element so that it is not rendered while the given layer is disabled
	 */
	public static LayeredDraw.Layer wrap(ResourceLocation layer, LayeredDraw.Layer element) {
		return (guiGraphics, deltaTracker) -> {
			if (!isDisabled(layer))
				element.render(guiGraphics, deltaTracker);
		};
	}

	public static boolean isDisabled(ResourceLocation layer) {
		return DISABLED_LAYERS.contains(layer);
	}

	public static void enable(ResourceLocation layer) {
		DISABLED_LAYERS.remove(layer);
	}

	public static void disable(ResourceLocation layer) {
		if (!isDisabled(layer))
			DISABLED_LAYERS.add(layer);
	}
}
