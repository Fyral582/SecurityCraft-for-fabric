package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.platform.SCBrewingRecipes;
import net.minecraft.world.item.ItemStack;

/**
 * Allows the inputs of SecurityCraft's brewing recipes to be put into the brewing stand's potion slots
 */
@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class BrewingStandPotionSlotMixin {
	@Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$mayPlaceItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (SCBrewingRecipes.isInput(stack))
			cir.setReturnValue(true);
	}
}
