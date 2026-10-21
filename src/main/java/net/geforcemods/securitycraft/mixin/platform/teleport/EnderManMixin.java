package net.geforcemods.securitycraft.mixin.platform.teleport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.world.entity.monster.EnderMan;

@Mixin(EnderMan.class)
public class EnderManMixin {
	@WrapOperation(method = "teleport(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;randomTeleport(DDDZ)Z"))
	private boolean securitycraft$onEnderTeleport(EnderMan enderMan, double x, double y, double z, boolean broadcast, Operation<Boolean> original) {
		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.EnderEntity(enderMan, x, y, z)))
			return false;

		return original.call(enderMan, x, y, z, broadcast);
	}
}
