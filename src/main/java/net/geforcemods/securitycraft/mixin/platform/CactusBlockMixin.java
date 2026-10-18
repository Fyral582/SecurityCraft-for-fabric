package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TriState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#canSustainPlant hook for cacti
 */
@Mixin(CactusBlock.class)
public class CactusBlockMixin {
	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void securitycraft$canSustainPlant(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockPos below = pos.below();
		TriState decision = level.getBlockState(below).canSustainPlant(level, below, Direction.UP, state);

		if (decision == TriState.DEFAULT)
			return;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (level.getBlockState(pos.relative(direction)).isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
				cir.setReturnValue(false);
				return;
			}
		}

		cir.setReturnValue(decision == TriState.TRUE && !level.getBlockState(pos.above()).liquid());
	}
}
