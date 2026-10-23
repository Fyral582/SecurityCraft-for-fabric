package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.misc.LayerToggleHandler;
import net.geforcemods.securitycraft.platform.gui.VanillaGuiLayers;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;

/**
 * Fabric API has no identified HUD layers in 1.21.2 and 1.21.3, so the HUD elements SecurityCraft hides get toggled here
 */
@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void securitycraft$toggleExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.CONTEXTUAL_INFO_BAR))
			ci.cancel();
	}

	@Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
	private void securitycraft$toggleJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.CONTEXTUAL_INFO_BAR))
			ci.cancel();
	}

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void securitycraft$toggleEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.EFFECTS))
			ci.cancel();
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
	private void securitycraft$toggleExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.EXPERIENCE_LEVEL))
			ci.cancel();
	}
}
