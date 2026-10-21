package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.geforcemods.securitycraft.platform.gui.VanillaGuiLayers;
import net.minecraft.resources.ResourceLocation;

public class LayerToggleHandler {
	private static final List<ResourceLocation> DISABLED_LAYERS = new ArrayList<>();

	private LayerToggleHandler() {}

	/**
	 * Makes the vanilla HUD elements that SecurityCraft may disable toggleable
	 */
	public static void init() {
		for (ResourceLocation layer : List.of(VanillaGuiLayers.CONTEXTUAL_INFO_BAR, VanillaGuiLayers.EFFECTS, VanillaGuiLayers.EXPERIENCE_LEVEL)) {
			HudElementRegistry.replaceElement(layer, element -> wrap(layer, element));
		}
	}

	/**
	 * Wraps a HUD element so that it is not rendered while the given layer is disabled
	 */
	public static HudElement wrap(ResourceLocation layer, HudElement element) {
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
