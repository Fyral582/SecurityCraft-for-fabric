package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#canSustainPlant hook for sugar cane
 */
@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {
	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void securitycraft$canSustainPlant(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockPos below = pos.below();
		BlockState belowState = level.getBlockState(below);

		if (belowState.is(state.getBlock()))
			return;

		TriState decision = belowState.canSustainPlant(level, below, Direction.UP, state);

		if (decision != TriState.DEFAULT)
			cir.setReturnValue(decision == TriState.TRUE);
	}
}
