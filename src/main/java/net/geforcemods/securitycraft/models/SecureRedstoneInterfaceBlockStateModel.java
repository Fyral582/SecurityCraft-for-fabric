package net.geforcemods.securitycraft.models;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class SecureRedstoneInterfaceBlockStateModel extends DisguisableBlockStateModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final Supplier<BlockStateModel> poweredModel;

	public SecureRedstoneInterfaceBlockStateModel(Supplier<BlockStateModel> poweredModel, BlockStateModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Override
	public void emitOldQuads(ModelData modelData, QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
		Boolean powered = modelData.get(POWERED);
		BlockStateModel model = powered != null && powered ? poweredModel.get() : null;

		if (model != null)
			model.emitQuads(emitter, level, pos, state, random, cullTest);
		else
			super.emitOldQuads(modelData, emitter, level, pos, state, random, cullTest);
	}

	@Override
	public TextureAtlasSprite oldParticleIcon(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Boolean powered = modelData.get(POWERED);
		BlockStateModel model = powered != null && powered ? poweredModel.get() : null;

		if (model != null)
			return model.particleSprite(level, pos, state);

		return super.oldParticleIcon(modelData, level, pos, state);
	}
}
