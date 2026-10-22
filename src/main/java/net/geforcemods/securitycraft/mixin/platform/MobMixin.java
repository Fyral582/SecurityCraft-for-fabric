package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Recreates SecurityCraft's LivingChangeTargetEvent handler
 */
@Mixin(Mob.class)
public class MobMixin {
	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onSetTarget(LivingEntity target, CallbackInfo ci) {
		if (target != null && SCEventHandler.onLivingSetAttackTarget(target))
			ci.cancel();
	}
}
