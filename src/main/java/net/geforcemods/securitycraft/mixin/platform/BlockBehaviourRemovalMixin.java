package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Emulates 1.21.5's BlockEntity#preRemoveSideEffects and BlockBehaviour#affectNeighborsAfterRemoval on 1.21.4, which both
 * replace parts of 1.21.4's BlockBehaviour#onRemove
 */
@Mixin(BlockBehaviour.class)
public class BlockBehaviourRemovalMixin {
	@Inject(method = "onRemove", at = @At("HEAD"))
	private void securitycraft$preRemoveSideEffects(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);

			if (be != null)
				be.preRemoveSideEffects(pos, state);
		}
	}

	@Inject(method = "onRemove", at = @At("TAIL"))
	private void securitycraft$affectNeighborsAfterRemoval(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
		if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel)
			state.getBlock().affectNeighborsAfterRemoval(state, serverLevel, pos, movedByPiston);
	}
}
