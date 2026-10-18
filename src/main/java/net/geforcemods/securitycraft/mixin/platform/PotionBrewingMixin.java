package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.platform.SCBrewingRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

/**
 * Adds support for SecurityCraft's item based brewing recipes
 */
@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private void securitycraft$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (SCBrewingRecipes.isIngredient(stack))
			cir.setReturnValue(true);
	}

	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private void securitycraft$hasMix(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		if (!SCBrewingRecipes.getOutput(input, ingredient).isEmpty())
			cir.setReturnValue(true);
	}

	@Inject(method = "mix", at = @At("HEAD"), cancellable = true)
	private void securitycraft$mix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack output = SCBrewingRecipes.getOutput(input, ingredient);

		if (!output.isEmpty())
			cir.setReturnValue(output);
	}
}
