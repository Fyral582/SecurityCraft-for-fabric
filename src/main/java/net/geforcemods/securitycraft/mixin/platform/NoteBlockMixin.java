package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recreates SecurityCraft's NoteBlockEvent.Play handler
 */
@Mixin(NoteBlock.class)
public class NoteBlockMixin {
	@Inject(method = "triggerEvent", at = @At("HEAD"))
	private void securitycraft$onNoteBlockPlayed(BlockState state, Level level, BlockPos pos, int id, int param, CallbackInfoReturnable<Boolean> cir) {
		SCEventHandler.onNoteBlockPlayed(level, pos, state.getValue(NoteBlock.NOTE), state.getValue(NoteBlock.INSTRUMENT));
	}
}
