package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's BlockEvent.EntityPlaceEvent (for SecurityCraft's handler) and the position aware place sound hook
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
	private void securitycraft$onBlockPlaced(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Local BlockPos pos, @Local Level level, @Local Player player, @Local(ordinal = 1) BlockState placedState) {
		SCEventHandler.onBlockEventPlace(level, player, pos, placedState);
	}

	@WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
	private SoundType securitycraft$getPlaceSoundType(BlockState state, Operation<SoundType> original, @Local BlockPos pos, @Local Level level, @Local Player player) {
		return state.getSoundType(level, pos, player);
	}

	@WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;getPlaceSound(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/sounds/SoundEvent;"))
	private SoundEvent securitycraft$getPlaceSound(BlockItem item, BlockState state, Operation<SoundEvent> original, @Local BlockPos pos, @Local Level level, @Local Player player) {
		SoundType soundType = state.getSoundType(level, pos, player);

		return soundType != state.getSoundType() ? soundType.getPlaceSound() : original.call(item, state);
	}
}
