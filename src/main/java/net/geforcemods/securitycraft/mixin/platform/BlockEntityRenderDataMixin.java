package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Hands the {@link ModelData} of block entities to models via Fabric's render data
 */
/**
 * The priority makes sure this wins against Fabric API's deprecated rendering-data-attachment module, which also implements
 * getRenderData in 1.21.4 and would otherwise make SecurityCraft's model data (e.g. disguises) disappear
 */
@Mixin(value = BlockEntity.class, priority = 1500)
public abstract class BlockEntityRenderDataMixin implements RenderDataBlockEntity {
	@Override
	public Object getRenderData() {
		ModelData modelData = ((BlockEntity) (Object) this).getModelData();

		return modelData == ModelData.EMPTY ? null : modelData;
	}
}
