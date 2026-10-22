package net.geforcemods.securitycraft.mixin.platform.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Recreates NeoForge's RenderHandEvent for SecurityCraft's handler
 */
@Mixin(GameRenderer.class)
public class GameRendererHandMixin {
	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onRenderHand(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
		if (SCClientEventHandler.renderHandEvent())
			ci.cancel();
	}
}
