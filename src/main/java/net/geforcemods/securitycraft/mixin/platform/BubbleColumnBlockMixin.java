package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates NeoForge's IBlockExtension#getBubbleColumnDirection hook
 */
@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
	@Inject(method = "getColumnState", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$getBubbleColumnDirection(Block bubbleColumn, BlockState belowState, BlockState occupyState, CallbackInfoReturnable<BlockState> cir) {
		if (!belowState.is(bubbleColumn)) {
			BlockState columnState = belowState.getBubbleColumnDirection().getColumnState();

			if (columnState != null)
				cir.setReturnValue(columnState);
		}
	}
}
