package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DisguisableBlockStateModel implements BlockStateModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BlockStateModel oldModel;

	public DisguisableBlockStateModel(BlockStateModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null && !disguisedState.is(Blocks.AIR)) {
			BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

			if (model != this) {
				ChunkSectionLayer layer = ItemBlockRenderTypes.getChunkRenderType(disguisedState);

				//the disguised model should be rendered in the render layer of the disguised block
				emitter.pushTransform(quad -> {
					quad.renderLayer(layer);
					return true;
				});
				model.emitQuads(emitter, level, pos, disguisedState, random, cullTest);
				emitter.popTransform();
				return;
			}
		}

		emitOldQuads(modelData, emitter, level, pos, state, random, cullTest);
	}

	public void emitOldQuads(ModelData modelData, QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		oldModel.emitQuads(emitter, level, pos, state, random, cullTest);
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> modelList) {
		oldModel.collectParts(random, modelList);
	}

	@Override
	public TextureAtlasSprite particleSprite(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null && !disguisedState.is(Blocks.AIR)) {
			BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

			if (model != this)
				return model.particleSprite(level, pos, disguisedState);
		}

		return oldParticleIcon(modelData, level, pos, state);
	}

	public TextureAtlasSprite oldParticleIcon(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return oldModel.particleSprite(level, pos, state);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return oldModel.particleIcon();
	}
}
