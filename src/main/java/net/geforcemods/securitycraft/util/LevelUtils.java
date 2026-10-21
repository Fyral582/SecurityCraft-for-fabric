package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.geforcemods.securitycraft.platform.SCPlatform;

public class LevelUtils {
	private LevelUtils() {}

	/**
	 * Correctly schedules a task for execution on the main thread depending on if the provided level is client- or serverside
	 */
	/**
	 * Replacement for NeoForge's Level#markAndNotifyBlock, which contains the update logic of Level#setBlock after the block
	 * has been changed in the chunk
	 */
	public static void markAndNotifyBlock(Level level, BlockPos pos, @Nullable LevelChunk chunk, BlockState oldState, BlockState newState, int flags, int recursionLeft) {
		Block block = newState.getBlock();
		BlockState currentState = level.getBlockState(pos);

		if (currentState == newState) {
			if (oldState != currentState)
				level.setBlocksDirty(pos, oldState, currentState);

			if ((flags & 2) != 0 && (!level.isClientSide() || (flags & 4) == 0) && (level.isClientSide() || chunk == null || chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)))
				level.sendBlockUpdated(pos, oldState, newState, flags);

			if ((flags & 1) != 0) {
				level.updateNeighborsAt(pos, oldState.getBlock());

				if (!level.isClientSide() && newState.hasAnalogOutputSignal())
					level.updateNeighbourForOutputSignal(pos, block);
			}

			if ((flags & 16) == 0 && recursionLeft > 0) {
				int i = flags & -34;

				oldState.updateIndirectNeighbourShapes(level, pos, i, recursionLeft - 1);
				newState.updateNeighbourShapes(level, pos, i, recursionLeft - 1);
				newState.updateIndirectNeighbourShapes(level, pos, i, recursionLeft - 1);
			}

			level.updatePOIOnBlockStateChange(pos, oldState, currentState);
		}
	}

	public static void addScheduledTask(LevelAccessor level, Runnable runnable) {
		if (level.isClientSide())
		Minecraft.getInstance().execute(runnable);
		else
			SCPlatform.getCurrentServer().execute(runnable);
	}

	public static LightningBolt createLightning(Level level, Vec3 pos, boolean effectOnly) {
		LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);

		lightning.snapTo(pos);
		lightning.setVisualOnly(effectOnly);
		return lightning;
	}

	/**
	 * Checks to see if the given coordinates are the same as the given GlobalPos' coordinates.
	 *
	 * @param pos The GlobalPos to check against
	 * @param coordinates a String[] which contains the x, y, and z coordinates, as well as the dimension ID of the view
	 * @return true if the two are the same, false otherwise
	 */
	public static boolean checkCoordinates(GlobalPos pos, String[] coordinates) {
		int xPos = Integer.parseInt(coordinates[0]);
		int yPos = Integer.parseInt(coordinates[1]);
		int zPos = Integer.parseInt(coordinates[2]);
		ResourceLocation dim = SecurityCraft.mcResLoc(coordinates.length == 4 ? coordinates[3] : "");

		return pos.pos().getX() == xPos && pos.pos().getY() == yPos && pos.pos().getZ() == zPos && pos.dimension().location().equals(dim);
	}

	/**
	 * @param pos The GlobalPos to use
	 * @return A formatted string of the GlobalPos' location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public static String toNBTString(GlobalPos pos) {
		return pos.pos().getX() + " " + pos.pos().getY() + " " + pos.pos().getZ() + " " + pos.dimension().location();
	}

	public static void blockEntityTicker(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		((ITickingBlockEntity) blockEntity).tick(level, pos, state);
	}
}