package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's player aware pick block hook (IBlockExtension#getCloneItemStack). Picking blocks is done on the client
 * in 1.21.2 and 1.21.3.
 */
@Mixin(Minecraft.class)
public class MinecraftPickBlockMixin {
	@Shadow
	public LocalPlayer player;

	@WrapOperation(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack securitycraft$getCloneItemStack(Block block, LevelReader level, BlockPos pos, BlockState state, Operation<ItemStack> original) {
		return state.getCloneItemStack(level, pos, player.getAbilities().instabuild && Screen.hasControlDown(), player);
	}
}
