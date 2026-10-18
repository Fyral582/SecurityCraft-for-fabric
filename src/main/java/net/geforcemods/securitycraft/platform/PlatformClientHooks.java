package net.geforcemods.securitycraft.platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlassBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

/**
 * Client side counterpart of {@link PlatformHooks}
 */
public final class PlatformClientHooks {
	private PlatformClientHooks() {}

	/**
	 * Creates a fluid render handler that renders the fluid like the given vanilla fluid
	 */
	private static FluidRenderHandler delegatingHandler(Fluid vanillaFluid) {
		return new FluidRenderHandler() {
			@Override
			public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
				return FluidRenderHandlerRegistry.INSTANCE.get(vanillaFluid).getFluidSprites(view, pos, vanillaFluid.defaultFluidState());
			}

			@Override
			public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
				return FluidRenderHandlerRegistry.INSTANCE.get(vanillaFluid).getFluidColor(view, pos, vanillaFluid.defaultFluidState());
			}
		};
	}

	public static void init() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(PlatformHooks::onBlockEntityLoad);
		ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> PlatformHooks.onChunkUnload(chunk));

		//NeoForge's fake fluids use the water and lava fluid types, which come with the vanilla textures and tints
		FluidRenderHandlerRegistry registry = FluidRenderHandlerRegistry.INSTANCE;

		registry.register(SCContent.FAKE_WATER.get(), SCContent.FLOWING_FAKE_WATER.get(), delegatingHandler(Fluids.WATER));
		registry.register(SCContent.FAKE_LAVA.get(), SCContent.FLOWING_FAKE_LAVA.get(), delegatingHandler(Fluids.LAVA));

		//replaces IBlockExtension#shouldDisplayFluidOverlay
		for (Block block : BuiltInRegistries.BLOCK) {
			if (block instanceof ReinforcedGlassBlock)
				registry.setBlockTransparency(block, true);
		}
	}
}
