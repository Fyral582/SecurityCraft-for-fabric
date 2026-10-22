package net.geforcemods.securitycraft.platform.ext;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Recreates the parts of NeoForge's IItemExtension that SecurityCraft uses. Injected into {@link net.minecraft.world.item.Item}.
 */
public interface SCItemExtension {
	/**
	 * Called before a block is activated when a player uses this item on it. Returning anything else than
	 * {@link InteractionResult#PASS} prevents the block from being activated.
	 */
	default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		return InteractionResult.PASS;
	}

	default boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return enchantment.value().isSupportedItem(stack);
	}

	default boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
		return enchantment.value().isPrimaryItem(stack);
	}
}
