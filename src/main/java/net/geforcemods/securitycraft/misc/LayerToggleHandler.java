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
	 * Makes the vanilla HUD layers that SecurityCraft may disable toggleable, and adds the given layer
	 */
	public static void init(ResourceLocation id, LayeredDraw.Layer layer) {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			for (ResourceLocation vanillaLayer : List.of(VanillaGuiLayers.EFFECTS, VanillaGuiLayers.EXPERIENCE_LEVEL)) {
				drawer.replaceLayer(vanillaLayer, original -> IdentifiedLayer.of(vanillaLayer, wrap(vanillaLayer, original)));
			}

			drawer.addLayer(IdentifiedLayer.of(id, wrap(id, layer)));
		});
	}

	/**
	 * Wraps a HUD layer so that it is not rendered while the given layer is disabled
	 */
	public static LayeredDraw.Layer wrap(ResourceLocation id, LayeredDraw.Layer layer) {
		return (guiGraphics, deltaTracker) -> {
			if (!isDisabled(id))
				layer.render(guiGraphics, deltaTracker);
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
