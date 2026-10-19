package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.platform.registry.DeferredHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;

/**
 * Vanilla's TypedInstance#is(Holder) (used by e.g. ItemStack and BlockState) compares holders by identity, which doesn't work for
 * SecurityCraft's registry holders (like on NeoForge). This makes the comparison work by comparing the actual values instead.
 */
@Mixin(TypedInstance.class)
public interface TypedInstanceHolderMixin<T> {
	@Inject(method = "is(Lnet/minecraft/core/Holder;)Z", at = @At("HEAD"), cancellable = true)
	private void securitycraft$compareDeferredHolders(Holder<T> type, CallbackInfoReturnable<Boolean> cir) {
		if (type instanceof DeferredHolder<?, ?> deferredHolder)
			cir.setReturnValue(deferredHolder.isBound() && ((TypedInstance<T>) this).typeHolder().value() == deferredHolder.value());
	}
}
