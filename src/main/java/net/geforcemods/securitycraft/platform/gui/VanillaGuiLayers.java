package net.geforcemods.securitycraft.platform.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

/**
 * Maps the names of NeoForge's VanillaGuiLayers that SecurityCraft uses to Fabric's vanilla HUD element ids
 */
public final class VanillaGuiLayers {
	public static final Identifier CONTEXTUAL_INFO_BAR = VanillaHudElements.INFO_BAR;
	public static final Identifier CONTEXTUAL_INFO_BAR_BACKGROUND = VanillaHudElements.INFO_BAR;
	public static final Identifier EFFECTS = VanillaHudElements.MOB_EFFECTS;
	public static final Identifier EXPERIENCE_LEVEL = VanillaHudElements.EXPERIENCE_LEVEL;

	private VanillaGuiLayers() {}
}
