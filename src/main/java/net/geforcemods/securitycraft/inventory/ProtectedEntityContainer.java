package net.geforcemods.securitycraft.inventory;

import java.util.stream.IntStream;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Wraps the container of an ownable entity so that hoppers can only extract from it when an extraction block allows it. This
 * replaces the NeoForge item handler capability of security sea boats.
 */
public class ProtectedEntityContainer<T extends Entity & Container & IOwnable> implements WorldlyContainer {
	private final T entity;
	private final int[] slots;

	public ProtectedEntityContainer(T entity) {
		this.entity = entity;
		slots = IntStream.range(0, entity.getContainerSize()).toArray();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return slots;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return entity.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(direction, entity, entity.level(), entity.blockPosition());
	}

	@Override
	public int getContainerSize() {
		return entity.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return entity.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return entity.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return entity.removeItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return entity.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		entity.setItem(slot, stack);
	}

	@Override
	public void setChanged() {
		entity.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return entity.stillValid(player);
	}

	@Override
	public void clearContent() {
		entity.clearContent();
	}
}
