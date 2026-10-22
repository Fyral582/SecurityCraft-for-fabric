package net.geforcemods.securitycraft.platform.gui;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Maps the names of NeoForge's VanillaGuiLayers that SecurityCraft uses to Fabric's vanilla HUD layer ids. The experience
 * bar and jump meter are part of the hotbar layer in 1.21.4, so they use an own id which is checked by GuiMixin.
 */
public final class VanillaGuiLayers {
	public static final ResourceLocation CONTEXTUAL_INFO_BAR = ResourceLocation.withDefaultNamespace("experience_bar");
	public static final ResourceLocation CONTEXTUAL_INFO_BAR_BACKGROUND = CONTEXTUAL_INFO_BAR;
	public static final ResourceLocation EFFECTS = IdentifiedLayer.STATUS_EFFECTS;
	public static final ResourceLocation EXPERIENCE_LEVEL = IdentifiedLayer.EXPERIENCE_LEVEL;

	private VanillaGuiLayers() {}
}
