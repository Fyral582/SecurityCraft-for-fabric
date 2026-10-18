package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.items.TaserItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer.HandRenderSelection;

/**
 * Makes sure that when a taser is held, only the hand that is holding the taser is rendered - prioritizing the main hand
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Inject(method = "evaluateWhichHandsToRender", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onlyRenderMainHandWhenHoldingTaser(LocalPlayer player, CallbackInfoReturnable<HandRenderSelection> cir) {
		if (player.getMainHandItem().getItem() instanceof TaserItem)
			cir.setReturnValue(HandRenderSelection.RENDER_MAIN_HAND_ONLY);
		else if (player.getOffhandItem().getItem() instanceof TaserItem)
			cir.setReturnValue(HandRenderSelection.RENDER_OFF_HAND_ONLY);
	}

	//replaces IClientItemExtensions#applyForgeHandTransform of the taser
	@WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 4))
	private void securitycraft$applyTaserHandTransform(ItemInHandRenderer renderer, PoseStack pose, HumanoidArm arm, float equippedProgress, Operation<Void> original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true, ordinal = 2) float swingProgress) {
		if (stack.getItem() instanceof TaserItem && swingProgress < 0.001F)
			pose.translate(0.02F, -0.4F, -0.5F);
		else
			original.call(renderer, pose, arm, equippedProgress);
	}
}
