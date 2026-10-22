package net.geforcemods.securitycraft.platform;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.platform.ext.AuxiliaryLightManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.portal.PortalShape;

/**
 * Wires up the NeoForge-like extension hooks that can be implemented with Fabric API callbacks instead of mixins
 */
public final class PlatformHooks {
	private PlatformHooks() {}

	public static void init() {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, level) -> onBlockEntityLoad(be, level));
		ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> onChunkUnload(chunk));
		EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
			boolean allowed = context == EnchantingContext.PRIMARY ? target.getItem().isPrimaryItemFor(target, enchantment) : target.getItem().supportsEnchantment(target, enchantment);

			return allowed ? TriState.DEFAULT : TriState.FALSE;
		});
		PortalShape.FRAME = (state, level, pos) -> state.isPortalFrame(level, pos);
	}

	public static void onBlockEntityLoad(BlockEntity be, Level level) {
		be.onLoad();

		//NeoForge saves auxiliary light values with the chunk. Since that is not done here, they are restored when the block entity loads
		IDisguisable.getDisguisedBlockState(be).ifPresent(disguisedState -> {
			int light = disguisedState.getLightEmission(level, be.getBlockPos());

			if (light > 0) {
				AuxiliaryLightManager lightManager = level.getAuxLightManager(be.getBlockPos());

				if (lightManager != null)
					lightManager.setLightAt(be.getBlockPos(), light);
			}
		});
	}

	public static void onChunkUnload(LevelChunk chunk) {
		for (BlockEntity be : chunk.getBlockEntities().values()) {
			be.onChunkUnloaded();
		}
	}
}
