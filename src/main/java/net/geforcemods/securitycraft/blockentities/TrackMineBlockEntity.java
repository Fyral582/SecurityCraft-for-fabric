package net.geforcemods.securitycraft.blockentities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.geforcemods.securitycraft.platform.nbt.ValueInput;
import net.geforcemods.securitycraft.platform.nbt.ValueOutput;

public class TrackMineBlockEntity extends OwnableBlockEntity {
	private boolean active = true;

	public TrackMineBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.TRACK_MINE_BLOCK_ENTITY.get(), pos, state);
	}

	public void activate() {
		if (!active) {
			active = true;
			setChanged();
		}
	}

	public void deactivate() {
		if (active) {
			active = false;
			setChanged();
		}
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
		ValueOutput tag = ValueOutput.of(compoundTag, lookupProvider);

		tag.putBoolean("TrackMineEnabled", active);
		super.saveAdditional(tag.tag(), tag.lookup());
	}

	@Override
	public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
		ValueInput tag = ValueInput.of(compoundTag, lookupProvider);

		super.loadAdditional(tag.tag(), tag.lookup());
		active = tag.getBooleanOr("TrackMineEnabled", true);
	}
}
