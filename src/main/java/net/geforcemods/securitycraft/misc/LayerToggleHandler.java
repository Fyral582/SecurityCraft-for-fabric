package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.geforcemods.securitycraft.platform.gui.VanillaGuiLayers;
import net.minecraft.resources.Identifier;

public class LayerToggleHandler {
	private static final List<Identifier> DISABLED_LAYERS = new ArrayList<>();

	private LayerToggleHandler() {}

	/**
	 * Makes the vanilla HUD elements that SecurityCraft may disable toggleable
	 */
	public static void init() {
		for (Identifier layer : List.of(VanillaGuiLayers.CONTEXTUAL_INFO_BAR, VanillaGuiLayers.EFFECTS, VanillaGuiLayers.EXPERIENCE_LEVEL)) {
			HudElementRegistry.replaceElement(layer, element -> wrap(layer, element));
		}
	}

	/**
	 * Wraps a HUD element so that it is not rendered while the given layer is disabled
	 */
	public static HudElement wrap(Identifier layer, HudElement element) {
		return (guiGraphics, deltaTracker) -> {
			if (!isDisabled(layer))
				element.extractRenderState(guiGraphics, deltaTracker);
		};
	}

	public static boolean isDisabled(Identifier layer) {
		return DISABLED_LAYERS.contains(layer);
	}

	public static void enable(Identifier layer) {
		DISABLED_LAYERS.remove(layer);
	}

	public static void disable(Identifier layer) {
		if (!isDisabled(layer))
			DISABLED_LAYERS.add(layer);
	}
}
