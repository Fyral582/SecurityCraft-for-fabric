package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;

/**
 * Recreates NeoForge's position aware light emission hook (IBlockExtension#getLightEmission)
 */
@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {
	@WrapOperation(method = "getEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
	private int securitycraft$getDynamicLightEmission(BlockState state, Operation<Integer> original, long packedPos) {
		if (state.hasDynamicLightEmission()) {
			BlockPos pos = BlockPos.of(packedPos);
			LightChunk chunk = ((LightEngine<?, ?>) (Object) this).chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));

			if (chunk != null)
				return state.getLightEmission(chunk, pos);
		}

		return original.call(state);
	}
}
