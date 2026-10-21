package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's position aware sound type hook for fall sounds and the IBlockExtension#isLadder hook
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@WrapOperation(method = "playBlockFallSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
	private SoundType securitycraft$getFallSoundType(BlockState state, Operation<SoundType> original) {
		LivingEntity self = (LivingEntity) (Object) this;

		return state.getSoundType(self.level(), BlockPos.containing(self.getX(), self.getY() - 0.2F, self.getZ()), (Entity) self);
	}

	@ModifyExpressionValue(method = "onClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
	private boolean securitycraft$isLadder(boolean original, @Local BlockState state, @Local BlockPos pos) {
		LivingEntity self = (LivingEntity) (Object) this;

		return original || state.isLadder(self.level(), pos, self);
	}
}
