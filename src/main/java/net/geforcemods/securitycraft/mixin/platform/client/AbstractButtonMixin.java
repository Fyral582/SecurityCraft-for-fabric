package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;

/**
 * Renders button labels in the button's foreground color if one has been set
 */
@Mixin(AbstractButton.class)
public class AbstractButtonMixin {
	@WrapOperation(method = "extractDefaultLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;extractScrollingStringOverContents(Lnet/minecraft/client/gui/ActiveTextCollector;Lnet/minecraft/network/chat/Component;I)V"))
	private void securitycraft$applyFGColor(AbstractButton button, ActiveTextCollector collector, Component text, int padding, Operation<Void> original) {
		int color = button.getFGColor();

		if ((color & 0xFFFFFF) != 0xFFFFFF && button.active)
			text = text.copy().withColor(color & 0xFFFFFF);

		original.call(button, collector, text, padding);
	}
}
