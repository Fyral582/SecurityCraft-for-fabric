package net.geforcemods.securitycraft.platform.ext;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

/**
 * Recreates the parts of NeoForge's IBlockExtension that SecurityCraft uses. This interface is injected into {@link Block} at
 * compile time via Loom's interface injection and at runtime via a mixin. The vanilla call sites are hooked by SecurityCraft's
 * mixins in the platform package.
 */
public interface SCBlockExtension {
	private Block self() {
		return (Block) this;
	}

	default ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		return state.getCloneItemStack(level, pos, includeData);
	}

	default boolean hasDynamicLightEmission(BlockState state) {
		return false;
	}

	default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getLightEmission();
	}

	default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return state.getSoundType();
	}

	default boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
		return state.isRedstoneConductor(level, pos);
	}

	default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (level.isClientSide())
			return level.setBlock(pos, fluid.createLegacyBlock(), 11);
		else
			return level.removeBlock(pos, false);
	}

	default boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		if (state.is(Blocks.REDSTONE_WIRE))
			return true;
		else if (state.is(Blocks.REPEATER)) {
			Direction facing = state.getValue(RepeaterBlock.FACING);

			return facing == direction || facing.getOpposite() == direction;
		}
		else if (state.is(Blocks.OBSERVER))
			return direction == state.getValue(ObserverBlock.FACING);
		else
			return state.isSignalSource() && direction != null;
	}

	default TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
		return TriState.DEFAULT;
	}

	default boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return false;
	}

	default void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {}

	default BubbleColumnDirection getBubbleColumnDirection(BlockState state) {
		if (state.is(Blocks.SOUL_SAND))
			return BubbleColumnDirection.UPWARD;
		else if (state.is(Blocks.MAGMA_BLOCK))
			return BubbleColumnDirection.DOWNWARD;
		else
			return BubbleColumnDirection.NONE;
	}

	default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return self().getExplosionResistance();
	}

	default boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return state.is(Blocks.PRISMARINE) || state.is(Blocks.PRISMARINE_BRICKS) || state.is(Blocks.SEA_LANTERN) || state.is(Blocks.DARK_PRISMARINE);
	}

	default boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(Blocks.OBSIDIAN);
	}

	default boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(Blocks.SCAFFOLDING);
	}

	default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(BlockTags.CLIMBABLE);
	}

	default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
	}

	@Nullable
	default PushReaction getPistonPushReaction(BlockState state) {
		return null;
	}
}
