package net.geforcemods.securitycraft.mixin.platform.client;

import java.io.File;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;

/**
 * Recreates NeoForge's ScreenshotEvent for SecurityCraft's handler
 */
@Mixin(Screenshot.class)
public class ScreenshotMixin {
	@Inject(method = "grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V", at = @At("HEAD"))
	private static void securitycraft$onScreenshot(File gameDirectory, String filename, RenderTarget renderTarget, int downscaleFactor, Consumer<Component> messageConsumer, CallbackInfo ci) {
		CameraController.onScreenshot();
	}
}
