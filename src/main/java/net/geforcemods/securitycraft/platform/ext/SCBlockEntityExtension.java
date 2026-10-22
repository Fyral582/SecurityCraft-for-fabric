package net.geforcemods.securitycraft.platform.ext;

import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.geforcemods.securitycraft.platform.model.ModelData;
import net.minecraft.network.Connection;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.geforcemods.securitycraft.platform.nbt.ValueInput;

/**
 * Recreates the parts of NeoForge's IBlockEntityExtension that SecurityCraft uses. Injected into {@link BlockEntity}.
 */
public interface SCBlockEntityExtension {
	private BlockEntity self() {
		return (BlockEntity) this;
	}

	/**
	 * Called on the client when the block entity's data packet is received
	 */
	default void onDataPacket(Connection net, ValueInput valueInput) {
		self().loadWithComponents(valueInput.tag(), valueInput.lookup());
	}

	/**
	 * Called when the chunk containing this block entity is unloaded
	 */
	default void onChunkUnloaded() {}

	/**
	 * Called when the block entity has been added to a loaded level
	 */
	default void onLoad() {
		requestModelDataUpdate();
	}

	default void requestModelDataUpdate() {
		BlockEntity be = self();

		if (be.getLevel() != null && be.getLevel().isClientSide())
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 8);
	}

	/**
	 * Backport of 1.21.5's BlockEntity#preRemoveSideEffects. Called by BlockBehaviourRemovalMixin before the block entity is
	 * removed because its block got replaced by a different block
	 */
	default void preRemoveSideEffects(BlockPos pos, BlockState state) {
		//1.21.5+ drops the contents of containers here. Contents that were already dropped by 1.21.4's onRemove are empty at this point
		if (this instanceof BaseContainerBlockEntity container && self().getLevel() != null)
			Containers.dropContents(self().getLevel(), pos, container);
	}

	default ModelData getModelData() {
		return ModelData.EMPTY;
	}
}
