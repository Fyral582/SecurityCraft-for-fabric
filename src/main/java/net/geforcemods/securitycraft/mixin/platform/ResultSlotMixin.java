package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.platform.CraftingPlayerTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Keeps track of the player that is taking a crafting result, like NeoForge does
 */
@Mixin(ResultSlot.class)
public class ResultSlotMixin {
	@Inject(method = "onTake", at = @At("HEAD"))
	private void securitycraft$setCraftingPlayer(Player player, ItemStack stack, CallbackInfo ci) {
		CraftingPlayerTracker.setCraftingPlayer(player);
	}

	@Inject(method = "onTake", at = @At("RETURN"))
	private void securitycraft$clearCraftingPlayer(Player player, ItemStack stack, CallbackInfo ci) {
		CraftingPlayerTracker.setCraftingPlayer(null);
	}
}
