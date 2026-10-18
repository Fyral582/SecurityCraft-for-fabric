package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.items.TaserItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;

/**
 * Gives players holding a taser a two-handed arm pose, and remembers that the player is holding a taser so that
 * {@link HumanoidModelMixin} can apply the actual taser arm pose. This replaces the arm pose enum extension used on NeoForge.
 */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
	@Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$getTaserArmPose(Avatar avatar, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
		if (stack.getItem() instanceof TaserItem)
			cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
	private void securitycraft$rememberTaser(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
		boolean holdsTaser = avatar.getMainHandItem().getItem() instanceof TaserItem || avatar.getOffhandItem().getItem() instanceof TaserItem;

		state.setData(HumanoidModelMixin.HOLDS_TASER, holdsTaser ? Boolean.TRUE : null);
	}
}
