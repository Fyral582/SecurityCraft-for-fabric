package net.geforcemods.securitycraft.blockentities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.geforcemods.securitycraft.platform.nbt.ValueInput;
import net.geforcemods.securitycraft.platform.nbt.ValueOutput;

public class ReinforcedIronBarsBlockEntity extends OwnableBlockEntity {
	private boolean canDrop = true;

	public ReinforcedIronBarsBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.REINFORCED_IRON_BARS_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
		ValueOutput tag = ValueOutput.of(compoundTag, lookupProvider);

		tag.putBoolean("canDrop", canDrop);
		super.saveAdditional(tag.tag(), tag.lookup());
	}

	@Override
	public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
		ValueInput tag = ValueInput.of(compoundTag, lookupProvider);

		super.loadAdditional(tag.tag(), tag.lookup());
		canDrop = tag.getBooleanOr("canDrop", true);
	}

	public boolean canDrop() {
		return canDrop;
	}

	public void setCanDrop(boolean canDrop) {
		this.canDrop = canDrop;
		setChanged();
	}
}
