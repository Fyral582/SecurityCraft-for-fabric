package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.platform.gui.ScreenLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * @see ScreenLayers
 */
@Mixin(Minecraft.class)
public class ScreenLayersMinecraftMixin {
	@Inject(method = "setScreen", at = @At("HEAD"))
	private void securitycraft$clearLayers(Screen screen, CallbackInfo ci) {
		ScreenLayers.onSetScreen();
	}

	@Inject(method = "resizeDisplay", at = @At("TAIL"))
	private void securitycraft$resizeLayers(CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;

		ScreenLayers.resize(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
	}
}
