package net.geforcemods.securitycraft.platform.ext;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Recreates the parts of NeoForge's IBlockStateExtension that SecurityCraft uses. Injected into {@link BlockState}.
 */
public interface SCBlockStateExtension {
	private BlockState self() {
		return (BlockState) this;
	}

	default ItemStack getCloneItemStack(LevelReader level, BlockPos pos, boolean includeData, Player player) {
		return self().getBlock().getCloneItemStack(level, pos, self(), includeData, player);
	}

	default boolean hasDynamicLightEmission() {
		return self().getBlock().hasDynamicLightEmission(self());
	}

	default int getLightEmission(BlockGetter level, BlockPos pos) {
		return self().getBlock().getLightEmission(self(), level, pos);
	}

	default SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return self().getBlock().getSoundType(self(), level, pos, entity);
	}

	default boolean shouldCheckWeakPower(SignalGetter level, BlockPos pos, Direction side) {
		return self().getBlock().shouldCheckWeakPower(self(), level, pos, side);
	}

	default boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		return self().getBlock().onDestroyedByPlayer(self(), level, pos, player, willHarvest, fluid);
	}

	default boolean canConnectRedstone(BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return self().getBlock().canConnectRedstone(self(), level, pos, direction);
	}

	default TriState canSustainPlant(BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
		return self().getBlock().canSustainPlant(self(), level, soilPosition, facing, plant);
	}

	default void onNeighborChange(LevelReader level, BlockPos pos, BlockPos neighbor) {
		self().getBlock().onNeighborChange(self(), level, pos, neighbor);
	}

	default BubbleColumnDirection getBubbleColumnDirection() {
		return self().getBlock().getBubbleColumnDirection(self());
	}

	default float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
		return self().getBlock().getExplosionResistance(self(), level, pos, explosion);
	}

	default boolean isConduitFrame(LevelReader level, BlockPos pos, BlockPos conduit) {
		return self().getBlock().isConduitFrame(self(), level, pos, conduit);
	}

	default boolean isPortalFrame(BlockGetter level, BlockPos pos) {
		return self().getBlock().isPortalFrame(self(), level, pos);
	}

	default boolean isScaffolding(LevelReader level, BlockPos pos, LivingEntity entity) {
		return self().getBlock().isScaffolding(self(), level, pos, entity);
	}

	default boolean isLadder(LevelReader level, BlockPos pos, LivingEntity entity) {
		return self().getBlock().isLadder(self(), level, pos, entity);
	}

	default boolean isSlimeBlock() {
		return self().is(Blocks.SLIME_BLOCK);
	}

	default boolean isStickyBlock() {
		return self().is(Blocks.SLIME_BLOCK) || self().is(Blocks.HONEY_BLOCK);
	}

	default boolean canStickTo(BlockState other) {
		if (self().is(Blocks.HONEY_BLOCK) && other.is(Blocks.SLIME_BLOCK))
			return false;
		else if (self().is(Blocks.SLIME_BLOCK) && other.is(Blocks.HONEY_BLOCK))
			return false;
		else
			return isStickyBlock() || other.isStickyBlock();
	}
}
