package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Applies the taser arm pose
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
	@Unique
	public static final RenderStateDataKey<Boolean> HOLDS_TASER = RenderStateDataKey.create(() -> "securitycraft:holds_taser");
	@Shadow
	@Final
	public ModelPart rightArm;
	@Shadow
	@Final
	public ModelPart leftArm;

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
	private void securitycraft$applyTaserArmPose(HumanoidRenderState state, CallbackInfo ci) {
		if (Boolean.TRUE.equals(state.getData(HOLDS_TASER))) {
			leftArm.yRot = 0.5F;
			rightArm.yRot = -0.5F;
			leftArm.xRot = rightArm.xRot = -1.5F;
		}
	}
}
