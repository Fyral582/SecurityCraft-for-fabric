package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;

/**
 * Renders button labels in the button's foreground color if one has been set
 */
@Mixin(AbstractButton.class)
public class AbstractButtonMixin {
	@WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;renderString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"))
	private void securitycraft$applyFGColor(AbstractButton button, GuiGraphics guiGraphics, Font font, int color, Operation<Void> original) {
		int fgColor = button.getFGColor();

		if ((fgColor & 0xFFFFFF) != 0xFFFFFF && button.active)
			color = (color & 0xFF000000) | (fgColor & 0xFFFFFF);

		original.call(button, guiGraphics, font, color);
	}
}
