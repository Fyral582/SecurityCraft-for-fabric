package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.misc.LayerToggleHandler;
import net.geforcemods.securitycraft.platform.gui.VanillaGuiLayers;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;

/**
 * Allows disabling the jump meter and the experience bar separately from the rest of the hotbar layer, like NeoForge's
 * separate gui layers
 */
@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
	private void securitycraft$disableJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.JUMP_METER))
			ci.cancel();
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void securitycraft$disableExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (LayerToggleHandler.isDisabled(VanillaGuiLayers.EXPERIENCE_BAR))
			ci.cancel();
	}
}
