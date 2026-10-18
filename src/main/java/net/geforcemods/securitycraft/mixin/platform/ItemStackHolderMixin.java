package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.platform.registry.DeferredHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Vanilla's ItemStack#is(Holder) compares holders by identity, which doesn't work for SecurityCraft's registry holders (like
 * on NeoForge). This makes the comparison work by comparing the actual items instead.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackHolderMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "is(Lnet/minecraft/core/Holder;)Z", at = @At("HEAD"), cancellable = true)
	private void securitycraft$compareDeferredHolders(Holder<Item> item, CallbackInfoReturnable<Boolean> cir) {
		if (item instanceof DeferredHolder<?, ?> deferredHolder)
			cir.setReturnValue(deferredHolder.isBound() && getItem() == deferredHolder.value());
	}
}
