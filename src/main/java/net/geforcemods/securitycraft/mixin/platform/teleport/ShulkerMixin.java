package net.geforcemods.securitycraft.mixin.platform.teleport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Shulker;

@Mixin(Shulker.class)
public class ShulkerMixin {
	@Inject(method = "teleportSomewhere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Shulker;unRide()V"), cancellable = true)
	private void securitycraft$onEnderTeleport(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos target) {
		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.EnderEntity((Shulker) (Object) this, target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D)))
			cir.setReturnValue(false);
	}
}
