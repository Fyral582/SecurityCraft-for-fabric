package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.geforcemods.securitycraft.platform.model.ModelProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DisguisableBlockStateModel implements BakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BakedModel oldModel;

	public DisguisableBlockStateModel(BakedModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null && !disguisedState.is(Blocks.AIR)) {
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

			if (model != this) {
				BlendMode blendMode = BlendMode.fromRenderLayer(ItemBlockRenderTypes.getChunkRenderType(disguisedState));
				MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();

				//the disguised model should be rendered in the render layer of the disguised block
				context.pushTransform(quad -> {
					if (quad.material().blendMode() == BlendMode.DEFAULT)
						quad.material(finder.clear().copyFrom(quad.material()).blendMode(blendMode).find());

					return true;
				});
				model.emitBlockQuads(level, disguisedState, pos, random, context);
				context.popTransform();
				return;
			}
		}

		emitOldQuads(modelData, level, state, pos, random, context);
	}

	public void emitOldQuads(ModelData modelData, BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
		oldModel.emitBlockQuads(level, state, pos, random, context);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
		return oldModel.getQuads(state, direction, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return oldModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return oldModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return oldModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return oldModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return oldModel.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return oldModel.getTransforms();
	}
}
