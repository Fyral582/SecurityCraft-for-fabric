package net.geforcemods.securitycraft.models;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class SecureRedstoneInterfaceBlockStateModel extends DisguisableBlockStateModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final BakedModel poweredModel;

	public SecureRedstoneInterfaceBlockStateModel(BakedModel poweredModel, BakedModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Override
	public void emitOldQuads(ModelData modelData, QuadEmitter emitter, BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, Predicate<@Nullable Direction> cullTest) {
		Boolean powered = modelData.get(POWERED);

		if (poweredModel != null && powered != null && powered)
			poweredModel.emitBlockQuads(emitter, level, state, pos, random, cullTest);
		else
			super.emitOldQuads(modelData, emitter, level, state, pos, random, cullTest);
	}
}
