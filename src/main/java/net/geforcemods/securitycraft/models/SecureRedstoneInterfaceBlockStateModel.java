package net.geforcemods.securitycraft.models;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
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
	public void emitOldQuads(ModelData modelData, BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
		Boolean powered = modelData.get(POWERED);

		if (poweredModel != null && powered != null && powered)
			poweredModel.emitBlockQuads(level, state, pos, random, context);
		else
			super.emitOldQuads(modelData, level, state, pos, random, context);
	}
}
