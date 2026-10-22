package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.renderer.LevelRenderer;

/**
 * Vanilla only renders the local player when the camera is attached to them. This makes the local player visible while they are
 * viewing through a security camera, so that they can see themselves when standing in front of it. This is not done while a frame
 * feed is being captured, since the player is moved to the camera's position during that time.
 */
@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererLocalPlayerMixin {
	@ModifyExpressionValue(method = "collectVisibleEntities", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/client/player/LocalPlayer"))
	private boolean securitycraft$renderLocalPlayerInCamera(boolean isLocalPlayer) {
		return isLocalPlayer && !(ClientHandler.isPlayerMountedOnCamera() && !FrameFeedHandler.isCapturingCamera());
	}
}
