package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.platform.SCBrewingRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

/**
 * Allows the inputs of SecurityCraft's brewing recipes to be inserted into the brewing stand's potion slots by automation
 */
@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {
	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
	private void securitycraft$canPlaceItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (slot >= 0 && slot < 3 && SCBrewingRecipes.isInput(stack) && ((BrewingStandBlockEntity) (Object) this).getItem(slot).isEmpty())
			cir.setReturnValue(true);
	}
}
