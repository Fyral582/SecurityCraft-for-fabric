package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates SecurityCraft's LivingDestroyBlockEvent handler, preventing withers from destroying reinforced blocks
 */
@Mixin(WitherBoss.class)
public class WitherBossMixin {
	@Inject(method = "canDestroy", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$preventDestroyingReinforcedBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof IReinforcedBlock)
			cir.setReturnValue(false);
	}
}
