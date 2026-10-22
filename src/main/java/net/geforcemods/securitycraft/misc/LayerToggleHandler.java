package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.geforcemods.securitycraft.platform.gui.VanillaGuiLayers;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class LayerToggleHandler {
	private static final List<ResourceLocation> DISABLED_LAYERS = new ArrayList<>();

	private LayerToggleHandler() {}

	/**
	 * Makes the vanilla HUD elements that SecurityCraft may disable toggleable. The experience bar and jump meter are not
	 * separate layers in 1.21.4, so they are toggled via GuiMixin instead.
	 */
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			for (ResourceLocation layer : List.of(VanillaGuiLayers.EFFECTS, VanillaGuiLayers.EXPERIENCE_LEVEL)) {
				drawer.replaceLayer(layer, element -> IdentifiedLayer.of(layer, wrap(layer, element)));
			}
		});
	}

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
