package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#isConduitFrame hook
 */
@Mixin(ConduitBlockEntity.class)
public class ConduitBlockEntityMixin {
	@WrapOperation(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private static boolean securitycraft$isConduitFrame(BlockState state, Block block, Operation<Boolean> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos conduitPos, @Local(ordinal = 1) BlockPos framePos) {
		if (original.call(state, block))
			return true;

		//only check once per position, and only for non-vanilla blocks, since vanilla blocks are already checked by the original code
		if (block == Blocks.PRISMARINE && !state.is(Blocks.PRISMARINE) && !state.is(Blocks.PRISMARINE_BRICKS) && !state.is(Blocks.SEA_LANTERN) && !state.is(Blocks.DARK_PRISMARINE))
			return state.isConduitFrame(level, framePos, conduitPos);

		return false;
	}
}
