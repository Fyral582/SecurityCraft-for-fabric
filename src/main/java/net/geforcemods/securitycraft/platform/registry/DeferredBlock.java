package net.geforcemods.securitycraft.platform.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> implements ItemLike {
	protected DeferredBlock(ResourceKey<Block> key) {
		super(key);
	}

	public static <T extends Block> DeferredBlock<T> createBlock(ResourceKey<Block> key) {
		return new DeferredBlock<>(key);
	}

	public ItemStack toStack() {
		return toStack(1);
	}

	public ItemStack toStack(int count) {
		ItemStack stack = asItem().getDefaultInstance();

		if (stack.isEmpty())
			throw new IllegalStateException("Block does not have a corresponding item: " + key);

		stack.setCount(count);
		return stack;
	}

	@Override
	public Item asItem() {
		return get().asItem();
	}
}
