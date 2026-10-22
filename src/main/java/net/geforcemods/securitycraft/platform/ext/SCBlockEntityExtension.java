package net.geforcemods.securitycraft.platform.ext;

import net.geforcemods.securitycraft.platform.model.ModelData;
import net.minecraft.network.Connection;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

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
	default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookupProvider) {
		CompoundTag tag = packet.getTag();

		if (!tag.isEmpty())
			self().loadWithComponents(tag, lookupProvider);
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

	default ModelData getModelData() {
		return ModelData.EMPTY;
	}
}
