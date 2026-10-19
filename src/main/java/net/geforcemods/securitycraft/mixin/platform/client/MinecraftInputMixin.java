package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/**
 * Recreates NeoForge's InputEvent.InteractionKeyMappingTriggered for SecurityCraft's handler
 */
@Mixin(Minecraft.class)
public class MinecraftInputMixin {
	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onStartAttack(CallbackInfoReturnable<Boolean> cir) {
		if (SCClientEventHandler.onClickInput(InteractionHand.MAIN_HAND, false))
			cir.setReturnValue(false);
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onContinueAttack(boolean leftClick, CallbackInfo ci) {
		if (leftClick && SCClientEventHandler.onClickInput(InteractionHand.MAIN_HAND, false))
			ci.cancel();
	}

	@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onStartUseItem(CallbackInfo ci) {
		if (SCClientEventHandler.onClickInput(InteractionHand.MAIN_HAND, true))
			ci.cancel();
	}

	@Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onPickBlock(CallbackInfo ci) {
		if (SCClientEventHandler.onClickInput(null, false))
			ci.cancel();
	}
}
