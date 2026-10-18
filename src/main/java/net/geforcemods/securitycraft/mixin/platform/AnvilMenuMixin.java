package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * Recreates SecurityCraft's AnvilUpdateEvent handler, preventing mending from being applied to universal block reinforcers
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
	@Shadow
	@Final
	private DataSlot cost;

	private AnvilMenuMixin(MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
		super(menuType, containerId, inventory, access, slotDefinition);
	}

	@Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onCreateResult(CallbackInfo ci) {
		if (SCEventHandler.onAnvilCraftPre(inputSlots.getItem(0), inputSlots.getItem(1))) {
			resultSlots.setItem(0, ItemStack.EMPTY);
			cost.set(0);
			ci.cancel();
		}
	}
}
