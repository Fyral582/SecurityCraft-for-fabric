package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#canConnectRedstone hook
 */
@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {
	@WrapOperation(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"))
	private boolean securitycraft$canConnectRedstone(BlockState state, Direction direction, Operation<Boolean> original, BlockGetter level, BlockPos pos, Direction directionArg, boolean nonNormalCubeAbove) {
		if (state.getBlock().getClass().getName().startsWith("net.geforcemods"))
			return state.canConnectRedstone(level, pos.relative(direction), direction);

		return original.call(state, direction);
	}
}
