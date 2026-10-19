package net.geforcemods.securitycraft.mixin.platform;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

/**
 * Recreates NeoForge's IBlockExtension#onTreeGrow hook
 */
@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {
	@Inject(method = "placeBelowTrunkBlock", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onTreeGrow(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo ci) {
		BlockState state = level.getBlockState(pos);

		if (state.getBlock().onTreeGrow(state, level, trunkSetter, random, pos, config))
			ci.cancel();
	}
}
