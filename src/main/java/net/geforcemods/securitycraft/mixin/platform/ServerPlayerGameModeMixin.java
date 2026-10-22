package net.geforcemods.securitycraft.mixin.platform;

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

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.platform.event.InteractionHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Recreates NeoForge's server side block interaction and block breaking hooks
 */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerLevel level;
	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onUseItemOnStart(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = InteractionHooks.onStart(player, hand, hitResult);

		if (result != null)
			cir.setReturnValue(result);
	}

	@Inject(method = "useItemOn", at = @At("RETURN"))
	private void securitycraft$onUseItemOnEnd(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionHooks.onEnd();
	}

	@ModifyVariable(method = "useItemOn", at = @At(value = "STORE"), ordinal = 1)
	private boolean securitycraft$modifySkipBlock(boolean original) {
		return InteractionHooks.shouldSkipBlock(original);
	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult securitycraft$beforeItemUse(ItemStack stack, UseOnContext context, Operation<InteractionResult> original) {
		InteractionResult result = InteractionHooks.beforeItemUse(context);

		return result != null ? result : original.call(stack, context);
	}

	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean securitycraft$harvestCheck(ServerPlayer player, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
		return SCEventHandler.onPlayerHarvestCheck(level, pos, original.call(player, state));
	}

	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
	private boolean securitycraft$onDestroyedByPlayer(ServerLevel level, BlockPos pos, boolean isMoving, Operation<Boolean> original, @Local(ordinal = 0) BlockState state) {
		boolean canHarvest = SCEventHandler.onPlayerHarvestCheck(level, pos, player.hasCorrectToolForDrops(state));

		return state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
	}
}
