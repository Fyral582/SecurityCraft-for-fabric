package net.geforcemods.securitycraft.mixin.platform.client;

import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * Recreates NeoForge's RenderHandEvent for SecurityCraft's handler
 */
@Mixin(GameRenderer.class)
public class GameRendererHandMixin {
	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onRenderHand(CameraRenderState cameraState, float partialTick, Matrix4fc modelViewMatrix, CallbackInfo ci) {
		if (SCClientEventHandler.renderHandEvent())
			ci.cancel();
	}
}
