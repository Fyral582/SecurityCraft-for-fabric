package net.geforcemods.securitycraft.platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlassBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

/**
 * Client side counterpart of {@link PlatformHooks}
 */
public final class PlatformClientHooks {
	private PlatformClientHooks() {}

	public static void init() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(PlatformHooks::onBlockEntityLoad);
		ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> PlatformHooks.onChunkUnload(chunk));

		//replaces IBlockExtension#shouldDisplayFluidOverlay
		for (Block block : BuiltInRegistries.BLOCK) {
			if (block instanceof ReinforcedGlassBlock)
				FluidRenderingRegistry.setBlockTransparency(block, true);
		}
	}
}
