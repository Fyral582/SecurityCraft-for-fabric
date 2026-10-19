package net.geforcemods.securitycraft.platform.ext;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.blockgetter.v2.FabricBlockGetter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * Recreates the parts of NeoForge's IBlockGetterExtension that SecurityCraft uses. Injected into {@link BlockGetter}.
 */
public interface SCBlockGetterExtension {
	default ModelData getModelData(BlockPos pos) {
		return ((FabricBlockGetter) this).getBlockEntityRenderData(pos) instanceof ModelData modelData ? modelData : ModelData.EMPTY;
	}

	@Nullable
	default AuxiliaryLightManager getAuxLightManager(BlockPos pos) {
		BlockGetter getter = (BlockGetter) this;

		if (getter instanceof LevelChunk chunk)
			return ((AuxLightHolder) chunk).securitycraft$getAuxLightManager();
		else if (getter instanceof ImposterProtoChunk imposter)
			return ((AuxLightHolder) imposter.getWrapped()).securitycraft$getAuxLightManager();
		else if (getter instanceof Level level) {
			ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);

			return chunk instanceof LevelChunk levelChunk ? ((AuxLightHolder) levelChunk).securitycraft$getAuxLightManager() : null;
		}

		return null;
	}

	interface AuxLightHolder {
		AuxiliaryLightManager securitycraft$getAuxLightManager();
	}
}
