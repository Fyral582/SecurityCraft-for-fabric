package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.platform.event.InteractionHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Recreates NeoForge's client side block interaction and block breaking hooks
 */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onUseItemOnStart(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = InteractionHooks.onStart(player, hand, hitResult);

		if (result != null)
			cir.setReturnValue(result);
	}

	@Inject(method = "performUseItemOn", at = @At("RETURN"))
	private void securitycraft$onUseItemOnEnd(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionHooks.onEnd();
	}

	@ModifyVariable(method = "performUseItemOn", at = @At(value = "STORE"), ordinal = 1)
	private boolean securitycraft$modifySkipBlock(boolean original) {
		return InteractionHooks.shouldSkipBlock(original);
	}

	@WrapOperation(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult securitycraft$beforeItemUse(ItemStack stack, UseOnContext context, Operation<InteractionResult> original) {
		InteractionResult result = InteractionHooks.beforeItemUse(context);

		return result != null ? result : original.call(stack, context);
	}

	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
	private boolean securitycraft$onDestroyedByPlayer(Level level, BlockPos pos, BlockState newState, int flags, Operation<Boolean> original, @Local(ordinal = 0) BlockState state, @Local FluidState fluidState) {
		return state.onDestroyedByPlayer(level, pos, minecraft.player, minecraft.player.getMainHandItem(), false, fluidState);
	}

	@WrapOperation(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
	private SoundType securitycraft$getHitSoundType(BlockState state, Operation<SoundType> original, @Local(argsOnly = true) BlockPos pos) {
		return state.getSoundType(minecraft.level, pos, minecraft.player);
	}
}
