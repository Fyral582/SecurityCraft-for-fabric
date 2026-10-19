package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DisguisableBlockStateModel implements BlockStateModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BlockStateModel oldModel;

	public DisguisableBlockStateModel(BlockStateModel oldModel) {
		this.oldModel = oldModel;
	}

	@Nullable
	private BlockStateModel getDisguisedModel(@Nullable BlockState disguisedState) {
		if (disguisedState != null && !disguisedState.is(Blocks.AIR)) {
			BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(disguisedState);

			if (model != this)
				return model;
		}

		return null;
	}

	@Override
	public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);
		BlockStateModel model = getDisguisedModel(disguisedState);

		if (model != null)
			model.emitQuads(emitter, level, pos, disguisedState, random, cullTest);
		else
			emitOldQuads(modelData, emitter, level, pos, state, random, cullTest);
	}

	public void emitOldQuads(ModelData modelData, QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		oldModel.emitQuads(emitter, level, pos, state, random, cullTest);
	}

	@Override
	public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
		oldModel.collectParts(random, parts);
	}

	@Override
	public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);
		BlockStateModel model = getDisguisedModel(disguisedState);

		if (model != null)
			return model.particleMaterial(level, pos, disguisedState);

		return oldParticleMaterial(modelData, level, pos, state);
	}

	public Material.Baked oldParticleMaterial(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return oldModel.particleMaterial(level, pos, state);
	}

	@Override
	public Material.Baked particleMaterial() {
		return oldModel.particleMaterial();
	}

	@Override
	public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);
		BlockStateModel model = getDisguisedModel(disguisedState);

		if (model != null)
			return model.materialFlags(level, pos, disguisedState, random);

		return oldMaterialFlags(modelData, level, pos, state, random);
	}

	public int oldMaterialFlags(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		return oldModel.materialFlags(level, pos, state, random);
	}

	@Override
	public int materialFlags() {
		return oldModel.materialFlags();
	}
}
