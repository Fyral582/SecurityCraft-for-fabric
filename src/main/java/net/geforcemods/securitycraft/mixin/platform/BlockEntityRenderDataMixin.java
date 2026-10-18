package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Hands the {@link ModelData} of block entities to models via Fabric's render data
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityRenderDataMixin implements RenderDataBlockEntity {
	@Override
	public Object getRenderData() {
		ModelData modelData = ((BlockEntity) (Object) this).getModelData();

		return modelData == ModelData.EMPTY ? null : modelData;
	}
}
