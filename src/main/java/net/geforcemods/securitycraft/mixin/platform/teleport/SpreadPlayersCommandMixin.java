package net.geforcemods.securitycraft.mixin.platform.teleport;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;

@Mixin(SpreadPlayersCommand.class)
public class SpreadPlayersCommandMixin {
	@WrapOperation(method = "setPlayerPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z"))
	private static boolean securitycraft$onSpreadPlayersCommand(Entity entity, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, boolean setCamera, Operation<Boolean> original) {
		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.SpreadPlayersCommand(entity, x, y, z)))
			return false;

		return original.call(entity, level, x, y, z, relatives, yRot, xRot, setCamera);
	}
}
