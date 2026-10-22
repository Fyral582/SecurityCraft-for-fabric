package net.geforcemods.securitycraft.mixin.platform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.geforcemods.securitycraft.platform.ext.AuxiliaryLightManager;
import net.geforcemods.securitycraft.platform.ext.SCBlockGetterExtension.AuxLightHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Adds an {@link AuxiliaryLightManager} to every level chunk, which stores light values of positions that are not tied to block
 * states (e.g. disguised blocks)
 */
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements AuxLightHolder {
	@Shadow
	public abstract Level getLevel();

	@Unique
	private final Map<BlockPos, Integer> securitycraft$auxLights = new ConcurrentHashMap<>();
	@Unique
	private final AuxiliaryLightManager securitycraft$auxLightManager = new AuxiliaryLightManager() {
		@Override
		public void setLightAt(BlockPos pos, int value) {
			Integer previous;

			if (value > 0)
				previous = securitycraft$auxLights.put(pos.immutable(), value);
			else
				previous = securitycraft$auxLights.remove(pos);

			if ((previous == null ? 0 : previous) != value)
				getLevel().getChunkSource().getLightEngine().checkBlock(pos);
		}

		@Override
		public int getLightAt(BlockPos pos) {
			return securitycraft$auxLights.getOrDefault(pos, 0);
		}
	};

	@Override
	public AuxiliaryLightManager securitycraft$getAuxLightManager() {
		return securitycraft$auxLightManager;
	}
}
