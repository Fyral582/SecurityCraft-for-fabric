package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's position aware sound type hook for block break sounds
 */
@Mixin(LevelEventHandler.class)
public class LevelEventHandlerMixin {
	@Shadow
	@Final
	private ClientLevel level;

	@WrapOperation(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
	private SoundType securitycraft$getBreakSoundType(BlockState state, Operation<SoundType> original, int type, BlockPos pos, int data) {
		return state.getSoundType(level, pos, null);
	}
}
