package net.geforcemods.securitycraft.mixin.platform.teleport;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
	@Inject(method = "performTeleport", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onTeleportCommand(CommandSourceStack source, Entity target, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, LookAt lookAt, CallbackInfo ci) {
		double targetX = relatives.contains(Relative.X) ? target.getX() + x : x;
		double targetY = relatives.contains(Relative.Y) ? target.getY() + y : y;
		double targetZ = relatives.contains(Relative.Z) ? target.getZ() + z : z;

		if (SCEventHandler.isTeleportCanceled(new EntityTeleportEvent.TeleportCommand(target, targetX, targetY, targetZ)))
			ci.cancel();
	}
}
