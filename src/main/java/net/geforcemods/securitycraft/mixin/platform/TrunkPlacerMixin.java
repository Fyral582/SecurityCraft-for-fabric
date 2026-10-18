package net.geforcemods.securitycraft.mixin.platform;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

/**
 * Recreates NeoForge's IBlockExtension#onTreeGrow hook
 */
@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {
	@Inject(method = "setDirtAt", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onTreeGrow(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo ci) {
		LevelReader levelReader = level instanceof LevelReader reader ? reader : null;

		if (level.isStateAtPosition(pos, state -> state.getBlock().onTreeGrow(state, levelReader, blockSetter, random, pos, config)))
			ci.cancel();
	}
}
