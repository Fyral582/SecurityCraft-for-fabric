package net.geforcemods.securitycraft.blocks.reinforced;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedDropperBlock extends ReinforcedDispenserBlock {
	private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

	public ReinforcedDropperBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public DispenseItemBehavior getDispenseMethod(Level level, ItemStack stack) {
		return DISPENSE_BEHAVIOUR;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedDropperBlockEntity(pos, state);
	}

	@Override
	protected void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDropperBlockEntity be) {
			BlockSource source = new BlockSource(level, pos, state, be);
			int randomSlot = be.getRandomSlot(level.getRandom());

			if (randomSlot < 0)
				level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
			else {
				ItemStack dispenseStack = be.getItem(randomSlot);

				if (!dispenseStack.isEmpty()) {
					Direction direction = level.getBlockState(pos).getValue(FACING);
					BlockPos targetPos = pos.relative(direction);
					Container container = HopperBlockEntity.getContainerAt(level, targetPos);
					ItemStack afterDispenseStack;

					if (container != null) {
						afterDispenseStack = HopperBlockEntity.addItem(be, container, dispenseStack.copyWithCount(1), direction.getOpposite());

						if (afterDispenseStack.isEmpty()) {
							afterDispenseStack = dispenseStack.copy();
							afterDispenseStack.shrink(1);
						}
						else
							afterDispenseStack = dispenseStack.copy();
					}
					else {
						Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, targetPos, direction.getOpposite());

						if (storage == null)
							afterDispenseStack = DISPENSE_BEHAVIOUR.dispense(source, dispenseStack);
						else {
							afterDispenseStack = dispenseStack.copy();

							try (Transaction transaction = Transaction.openOuter()) {
								if (storage.insert(ItemVariant.of(dispenseStack), 1, transaction) == 1) {
									transaction.commit();
									afterDispenseStack.shrink(1);
								}
							}
						}
					}

					be.setItem(randomSlot, afterDispenseStack);
				}
			}
		}
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.DROPPER;
	}
}
