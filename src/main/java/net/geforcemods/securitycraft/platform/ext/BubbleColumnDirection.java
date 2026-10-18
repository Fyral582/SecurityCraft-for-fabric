package net.geforcemods.securitycraft.platform.ext;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fabric replacement for NeoForge's BubbleColumnDirection
 */
public enum BubbleColumnDirection {
	UPWARD(() -> Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, false)),
	DOWNWARD(() -> Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, true)),
	NONE(() -> null);

	private final java.util.function.Supplier<BlockState> stateSupplier;

	BubbleColumnDirection(java.util.function.Supplier<BlockState> stateSupplier) {
		this.stateSupplier = stateSupplier;
	}

	@Nullable
	public BlockState getColumnState() {
		return stateSupplier.get();
	}
}
