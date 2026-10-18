package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.entity.AbstractSecuritySeaBoat;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Allows security sea boats to float on lava. This replaces NeoForge's IAbstractBoatExtension#canBoatInFluid.
 */
@Mixin(AbstractBoat.class)
public class AbstractBoatMixin {
	@WrapOperation(method = {"getWaterLevelAbove", "checkInWater", "isUnderwater", "checkFallDamage"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean securitycraft$canBoatInLava(FluidState state, TagKey<Fluid> tag, Operation<Boolean> original) {
		if (original.call(state, tag))
			return true;

		return (Object) this instanceof AbstractSecuritySeaBoat && tag == FluidTags.WATER && state.is(FluidTags.LAVA);
	}
}
