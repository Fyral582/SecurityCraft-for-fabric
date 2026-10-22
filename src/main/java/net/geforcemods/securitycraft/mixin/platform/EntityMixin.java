package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's EntityMountEvent (for dismounting) and the position aware sound type hook for step sounds
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	private Entity vehicle;

	@Shadow
	public abstract Level level();

	@Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onDismount(CallbackInfo ci) {
		if (vehicle != null && SCEventHandler.onDismount((Entity) (Object) this, vehicle))
			ci.cancel();
	}

	@WrapOperation(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
	private SoundType securitycraft$getStepSoundType(BlockState state, Operation<SoundType> original, BlockPos pos, BlockState stateArg) {
		return state.getSoundType(level(), pos, (Entity) (Object) this);
	}
}
