package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.platform.TaserRenderData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Gives players holding a taser a two-handed arm pose, and remembers that the player is holding a taser so that
 * {@link HumanoidModelMixin} can apply the actual taser arm pose. This replaces the arm pose enum extension used on NeoForge.
 */
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
	@Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$getTaserArmPose(Player player, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
		if (stack.getItem() instanceof TaserItem)
			cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
	private void securitycraft$rememberTaser(AbstractClientPlayer player, PlayerRenderState state, float partialTick, CallbackInfo ci) {
		boolean holdsTaser = player.getMainHandItem().getItem() instanceof TaserItem || player.getOffhandItem().getItem() instanceof TaserItem;

		((TaserRenderData) state).securitycraft$setHoldsTaser(holdsTaser);
	}
}
