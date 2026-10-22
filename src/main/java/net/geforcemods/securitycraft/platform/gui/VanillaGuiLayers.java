package net.geforcemods.securitycraft.platform.gui;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Maps the names of NeoForge's VanillaGuiLayers that SecurityCraft uses to Fabric's vanilla HUD layer ids. The jump meter
 * and the experience bar are part of the hotbar layer in vanilla, so they get their own ids that are handled by GuiMixin.
 */
public final class VanillaGuiLayers {
	public static final ResourceLocation EFFECTS = IdentifiedLayer.STATUS_EFFECTS;
	public static final ResourceLocation EXPERIENCE_LEVEL = IdentifiedLayer.EXPERIENCE_LEVEL;
	public static final ResourceLocation JUMP_METER = ResourceLocation.fromNamespaceAndPath("securitycraft", "vanilla_jump_meter");
	public static final ResourceLocation EXPERIENCE_BAR = ResourceLocation.fromNamespaceAndPath("securitycraft", "vanilla_experience_bar");

	private VanillaGuiLayers() {}
}
