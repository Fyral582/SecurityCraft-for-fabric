package net.geforcemods.securitycraft.platform.ext;

import net.minecraft.core.BlockPos;

/**
 * Fabric replacement for NeoForge's AuxiliaryLightManager. Allows setting light values at specific positions without them
 * being tied to a block state.
 */
public interface AuxiliaryLightManager {
	void setLightAt(BlockPos pos, int value);

	default void removeLightAt(BlockPos pos) {
		setLightAt(pos, 0);
	}

	int getLightAt(BlockPos pos);
}
