package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#shouldCheckWeakPower and IBlockExtension#onNeighborChange hooks
 */
@Mixin(Level.class)
public abstract class LevelMixin implements SignalGetter {
	@Override
	public int getSignal(BlockPos pos, Direction direction) {
		BlockState state = getBlockState(pos);
		int signal = state.getSignal(this, pos, direction);

		return state.shouldCheckWeakPower(this, pos, direction) ? Math.max(signal, getDirectSignalTo(pos)) : signal;
	}

	@Inject(method = "updateNeighbourForOutputSignal", at = @At("HEAD"))
	private void securitycraft$onNeighborChange(BlockPos pos, Block block, CallbackInfo ci) {
		Level level = (Level) (Object) this;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos neighbor = pos.relative(direction);

			if (level.hasChunkAt(neighbor))
				level.getBlockState(neighbor).onNeighborChange(level, neighbor, pos);
		}
	}
}
