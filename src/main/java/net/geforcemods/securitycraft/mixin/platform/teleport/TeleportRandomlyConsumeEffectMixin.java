package net.geforcemods.securitycraft.mixin.platform.teleport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;

@Mixin(TeleportRandomlyConsumeEffect.class)
public class TeleportRandomlyConsumeEffectMixin {
	@WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z"))
	private boolean securitycraft$onItemConsumptionTeleport(LivingEntity entity, double x, double y, double z, boolean broadcast, Operation<Boolean> original) {
		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.ItemConsumption(entity, x, y, z)))
			return false;

		return original.call(entity, x, y, z, broadcast);
	}
}
