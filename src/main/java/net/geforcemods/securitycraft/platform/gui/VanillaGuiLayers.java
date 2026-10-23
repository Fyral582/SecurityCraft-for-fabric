package net.geforcemods.securitycraft.platform.gui;

import net.minecraft.resources.ResourceLocation;

/**
 * Ids for the vanilla HUD elements that SecurityCraft toggles. Fabric API has no HUD layer ids in 1.21.2 and 1.21.3, so these
 * are only checked by GuiMixin.
 */
public final class VanillaGuiLayers {
	public static final ResourceLocation CONTEXTUAL_INFO_BAR = ResourceLocation.withDefaultNamespace("experience_bar");
	public static final ResourceLocation CONTEXTUAL_INFO_BAR_BACKGROUND = CONTEXTUAL_INFO_BAR;
	public static final ResourceLocation EFFECTS = ResourceLocation.withDefaultNamespace("status_effects");
	public static final ResourceLocation EXPERIENCE_LEVEL = ResourceLocation.withDefaultNamespace("experience_level");

	private VanillaGuiLayers() {}
}
