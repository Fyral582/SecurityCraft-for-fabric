package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's player aware pick block hook (IBlockExtension#getCloneItemStack)
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@WrapOperation(method = "handlePickItemFromBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack securitycraft$getCloneItemStack(BlockState state, LevelReader level, BlockPos pos, boolean includeData, Operation<ItemStack> original) {
		return state.getCloneItemStack(level, pos, includeData, player);
	}
}
