package net.geforcemods.securitycraft.mixin.platform.teleport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.portal.TeleportTransition;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
	@WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;"))
	private ServerPlayer securitycraft$onEnderPearlLand(ServerPlayer player, TeleportTransition transition, Operation<ServerPlayer> original) {
		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.EnderPearl(player, transition.position().x, transition.position().y, transition.position().z)))
			return null;

		return original.call(player, transition);
	}
}
