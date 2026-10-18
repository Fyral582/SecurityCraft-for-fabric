package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.platform.gui.ScreenLayers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Renders the screens below the current GUI layer
 *
 * @see ScreenLayers
 */
@Mixin(GameRenderer.class)
public class ScreenLayersGameRendererMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
	private void securitycraft$renderLayers(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
		ScreenLayers.renderLayers(guiGraphics, partialTick);
		original.call(screen, guiGraphics, mouseX, mouseY, partialTick);
	}
}
