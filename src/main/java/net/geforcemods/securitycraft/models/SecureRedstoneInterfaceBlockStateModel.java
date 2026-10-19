package net.geforcemods.securitycraft.models;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SecureRedstoneInterfaceBlockStateModel extends DisguisableBlockStateModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final Supplier<BlockStateModel> poweredModel;

	public SecureRedstoneInterfaceBlockStateModel(Supplier<BlockStateModel> poweredModel, BlockStateModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Nullable
	private BlockStateModel getPoweredModel(ModelData modelData) {
		Boolean powered = modelData.get(POWERED);

		return powered != null && powered ? poweredModel.get() : null;
	}

	@Override
	public void emitOldQuads(ModelData modelData, QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		BlockStateModel model = getPoweredModel(modelData);

		if (model != null)
			model.emitQuads(emitter, level, pos, state, random, cullTest);
		else
			super.emitOldQuads(modelData, emitter, level, pos, state, random, cullTest);
	}

	@Override
	public Material.Baked oldParticleMaterial(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		BlockStateModel model = getPoweredModel(modelData);

		if (model != null)
			return model.particleMaterial(level, pos, state);

		return super.oldParticleMaterial(modelData, level, pos, state);
	}

	@Override
	public int oldMaterialFlags(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		BlockStateModel model = getPoweredModel(modelData);

		if (model != null)
			return model.materialFlags(level, pos, state, random);

		return super.oldMaterialFlags(modelData, level, pos, state, random);
	}
}
