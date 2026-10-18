package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.MutablePair;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.components.Notes.NoteWrapper;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.SendManualPages;
import net.geforcemods.securitycraft.platform.event.EntityTeleportEvent;
import net.geforcemods.securitycraft.platform.event.PlayerInteractEvent;
import net.geforcemods.securitycraft.platform.network.PacketDistributor;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Common event handling. On Fabric, the handlers are wired up to Fabric API callbacks in {@link #register()} or called from
 * SecurityCraft's own mixins.
 */
public class SCEventHandler {
	private static final Integer NOTE_DELAY = 9;
	public static final Map<Player, MutablePair<Integer, List<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();
	public static final Map<ResourceKey<Level>, List<ChunkAccess>> TINT_UPDATE_QUEUE = new HashMap<>();

	private SCEventHandler() {}

	public static void register() {
		ServerWorldEvents.LOAD.register((server, level) -> onLevelLoad(level));
		ServerTickEvents.START_SERVER_TICK.register(SCEventHandler::onServerTickPre);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> onDatapackSync(player));
		ServerEntityEvents.ENTITY_UNLOAD.register(SCEventHandler::onEntityLeaveLevel);
		ServerLifecycleEvents.SERVER_STARTING.register(SCEventHandler::onServerAboutToStart);
		ServerWorldEvents.UNLOAD.register((server, level) -> onLevelUnload(level));
		ServerLifecycleEvents.SERVER_STOPPED.register(SCEventHandler::onServerStop);
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> !onLivingAttacked(entity, source));
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> onDamageTaken(entity, source));
		AttackBlockCallback.EVENT.register(SCEventHandler::onLeftClickBlock);
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, be) -> !onBlockEventBreak(level, player, pos, state));
		UseItemCallback.EVENT.register(SCEventHandler::onRightClickItem);
		FuelRegistryEvents.EXCLUSIONS.register((builder, context) -> onFurnaceFuelBurnTime(builder));
	}

	public static void onServerTickPre(MinecraftServer server) {
		SecurityCameraBlockEntity.resetForceLoadingCounter();

		if (!server.tickRateManager().isFrozen() || server.tickRateManager().isSteppingForward()) {
			PLAYING_TUNES.forEach((player, pair) -> {
				int ticksRemaining = pair.getLeft();

				if (ticksRemaining == 0) {
					if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.PORTABLE_TUNE_PLAYER.get()).isEmpty()) {
						pair.setLeft(-1);
						return;
					}

					if (!pair.getRight().isEmpty()) {
						NoteWrapper note = pair.getRight().removeFirst();

						if (note != null) {
							NoteBlockInstrument instrument = NoteBlockInstrument.valueOf(note.instrumentName().toUpperCase());
							SoundEvent sound = instrument.hasCustomSound() && !note.customSound().isEmpty() ? SoundEvent.createVariableRangeEvent(Identifier.parse(note.customSound())) : instrument.getSoundEvent().value();
							float pitch = instrument.isTunable() ? (float) Math.pow(2.0D, (note.id() - 12) / 12.0D) : 1.0F;

							player.level().playSound(null, player.blockPosition(), sound, SoundSource.RECORDS, 3.0F, pitch);
							handlePlayedNote(player.level(), player.blockPosition(), note.id(), instrument, note.customSound());
							player.gameEvent(GameEvent.NOTE_BLOCK_PLAY);
							pair.setLeft(NOTE_DELAY);
							return;
						}
					}

					pair.setLeft(-1);
				}
				else
					pair.setLeft(ticksRemaining - 1);
			});

			//remove finished tunes
			if (PLAYING_TUNES.size() > 0) {
				Iterator<Entry<Player, MutablePair<Integer, List<NoteWrapper>>>> entries = PLAYING_TUNES.entrySet().iterator();

				while (entries.hasNext()) {
					if (entries.next().getValue().left == -1)
						entries.remove();
				}
			}

			for (ResourceKey<Level> levelResourceKey : TINT_UPDATE_QUEUE.keySet()) {
				List<ChunkAccess> chunksToRecompile = TINT_UPDATE_QUEUE.get(levelResourceKey);

				if (!chunksToRecompile.isEmpty()) {
					server.getLevel(levelResourceKey).getChunkSource().chunkMap.resendBiomesForChunks(chunksToRecompile); //Tells the client to mark all modified sections as dirty, to properly update block tints. /fillbiome uses this too
					chunksToRecompile.clear();
				}
			}
		}
	}

	public static void onDatapackSync(ServerPlayer player) {
		if (player == null)
			PacketDistributor.sendToAllPlayers(new SendManualPages(SCManualItem.PAGES));
		else
			PacketDistributor.sendToPlayer(player, new SendManualPages(SCManualItem.PAGES));
	}

	public static void onEntityLeaveLevel(Entity entity, ServerLevel level) {
		if (entity instanceof ServerPlayer player) {
			if (player.getCamera() instanceof SecurityCamera cam) {
				if (player.getEffect(MobEffects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
					player.removeEffect(MobEffects.NIGHT_VISION);

				if (level.getBlockEntity(cam.blockPosition()) instanceof SecurityCameraBlockEntity camBe)
					camBe.stopViewing();

				cam.discard();
			}

			for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.getCameraFeedChunks(player) != null || be.hasPlayerFrameLink(player))) {
				viewedCamera.unlinkFrameForPlayer(player.getUUID(), null);
				viewedCamera.clearCameraFeedChunks(player);
			}
		}
	}

	public static void onServerAboutToStart(MinecraftServer server) {
		PasscodeUtils.startHashingThread(server);
	}

	public static void onLevelLoad(ServerLevel level) {
		if (level.dimension() == Level.OVERWORLD)
			SaltData.refreshLevel(level);
	}

	public static void onLevelUnload(ServerLevel level) {
		if (level.dimension() == Level.OVERWORLD) {
			SaltData.invalidate();
			BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.clear();
		}
	}

	public static void onServerStop(MinecraftServer server) {
		PasscodeUtils.stopHashingThread();
	}

	/**
	 * @return true if the incoming damage should be canceled
	 */
	public static boolean onLivingAttacked(LivingEntity entity, DamageSource damageSource) {
		if (entity instanceof ServerPlayer player) {
			ServerLevel level = player.level();

			if (!player.isCreative() && damageSource.equals(level.damageSources().inWall()) && !player.isInvulnerableTo(level, damageSource) && BlockUtils.isInsideUnownedReinforcedBlocks(level, player, player.getEyePosition(), player.getBbWidth())) {
				int reinforcedSuffocationDamage = ConfigHandler.SERVER.reinforcedSuffocationDamage.get();

				if (reinforcedSuffocationDamage != -1) {
					player.hurt(CustomDamageSources.inReinforcedWall(level.registryAccess()), reinforcedSuffocationDamage);
					return true;
				}
			}
		}

		return false;
	}

	public static void onDamageTaken(LivingEntity entity, DamageSource source) {
		Level level = entity.level();

		if (source.is(CustomDamageSources.ELECTRICITY))
			level.playSound(null, entity.blockPosition(), SCSounds.ELECTRIFIED.event, SoundSource.BLOCKS, 0.25F, 1.0F);

		if (!level.isClientSide() && entity instanceof ServerPlayer player && PlayerUtils.isPlayerMountedOnCamera(entity))
			((SecurityCamera) player.getCamera()).stopViewing(player);
	}

	/**
	 * Called when an entity is about to dismount another entity
	 *
	 * @return true if the dismounting should be canceled
	 */
	public static boolean onDismount(Entity entityMounting, Entity entityBeingMounted) {
		if (ConfigHandler.SERVER.preventReinforcedFloorGlitching.get() && entityBeingMounted instanceof Boat boat && entityMounting instanceof Player player && !player.getAbilities().invulnerable) {
			Vec3 incorrectDismountLocation = new Vec3(boat.getX(), boat.getBoundingBox().maxY, boat.getZ());
			Vec3 dismountLocation = boat.getDismountLocationForPassenger(player);
			Vec3 newCenterPos = dismountLocation.add(0.0F, player.getBbHeight() / 2, 0.0F);
			Vec3 newEyePos = dismountLocation.add(0.0F, player.getEyeHeight(), 0.0F);

			if (dismountLocation.equals(incorrectDismountLocation) && (BlockUtils.isInsideUnownedReinforcedBlocks(player.level(), player, newEyePos, player.getBbWidth()) || BlockUtils.isInsideUnownedReinforcedBlocks(player.level(), player, newCenterPos, player.getBbWidth()))) {
				player.setYRot(boat.getYRot() + 180.0F % 360.0F); //This doesn't actually alter the player's rotation, the y-rotation is only changed for the calculation of the new dismount location behind the boat in the next line
				dismountLocation = boat.getDismountLocationForPassenger(player);

				if (!dismountLocation.equals(incorrectDismountLocation))
					player.setPos(dismountLocation);
				else
					return true;
			}
		}

		return false;
	}

	/**
	 * Called right before an item is used on a block, after the block itself did not consume the interaction
	 *
	 * @return The result to return instead of using the item, or null if the item should be used normally
	 */
	public static InteractionResult onUseItemOnBlock(Player player, Level level, BlockPos pos, ItemStack stack) {
		if (stack.is(Items.WRITABLE_BOOK) || stack.is(Items.WRITTEN_BOOK)) {
			BlockState state = level.getBlockState(pos);

			if (state.is(SCContent.REINFORCED_LECTERN.get())) {
				ReinforcedLecternBlockEntity be = (ReinforcedLecternBlockEntity) level.getBlockEntity(pos);

				if (be.isOwnedBy(player) && LecternBlock.tryPlaceBook(player, level, pos, state, stack)) {
					player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
					return InteractionResult.SUCCESS;
				}

				return InteractionResult.PASS;
			}
		}

		return null;
	}

	/**
	 * Fires both right click block handlers in the order they were called on NeoForge
	 */
	public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
		highestPriorityOnRightClickBlock(event);

		if (!event.isCanceled())
			onRightClickBlock(event);
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if (!stack.isEmpty() && !(stack.getItem() instanceof BlockItem) && !stack.is(SCTags.Items.CAN_INTERACT_WITH_DOORS)) {
			Block block = event.getLevel().getBlockState(event.getPos()).getBlock();

			if (block == SCContent.KEYPAD_DOOR.get())
				event.setUseItem(TriState.FALSE);
			else if (block == SCContent.REINFORCED_DOOR.get() || block == SCContent.REINFORCED_IRON_TRAPDOOR.get() || block == SCContent.SCANNER_DOOR.get())
				event.setCanceled(true);
		}
	}

	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			event.setCanceled(true);
			return;
		}

		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockEntity be = level.getBlockEntity(pos);
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (be instanceof ILockable lockable && lockable.isLockedBySSS() && lockable.disableInteractionWhenLocked(level, pos, player) && !player.isShiftKeyDown()) {
			if (event.getHand() == InteractionHand.MAIN_HAND) {
				MutableComponent blockName = Utils.localize(block.getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
			}

			event.setCanceled(true);
			return;
		}

		if (be instanceof IOwnable ownable) {
			Owner owner = ownable.getOwner();

			if (!owner.isValidated()) {
				if (ownable.isOwnedBy(player)) {
					owner.setValidated(true);
					ownable.onValidate();
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getDescriptionId()), Component.translatable("messages.securitycraft:ownable.validate"), ChatFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getDescriptionId()), Component.translatable("messages.securitycraft:ownable.ownerNotValidated"), ChatFormatting.RED);

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
				return;
			}
		}

		if (!level.isClientSide()) {
			if (event.getItemStack().is(Items.REDSTONE) && be instanceof IEMPAffected empAffected && empAffected.isShutDown()) {
				empAffected.reactivate();

				if (!player.isCreative())
					event.getItemStack().shrink(1);

				player.swing(event.getHand());
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
				return;
			}

			ItemStack heldItem = player.getItemInHand(event.getHand());

			if (heldItem.is(SCContent.KEY_PANEL.get()) && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
				for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
					if (pc.isUnprotectedBlock(state)) {
						event.setUseBlock(TriState.FALSE);
						event.setUseItem(TriState.TRUE);
					}
				}

				return;
			}

			if (heldItem.is(SCContent.CODEBREAKER.get()) && level.getBlockEntity(pos) instanceof ICodebreakable codebreakable) {
				codebreakable.handleCodebreaking(player, event.getHand());
				event.setCanceled(true);
				return;
			}
		}

		if (block instanceof DisplayCaseBlock && player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty()) {
			event.setUseBlock(TriState.TRUE);
			event.setUseItem(TriState.FALSE);
			return;
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(pos));

		if (!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(player, event.getHand()) == InteractionResult.SUCCESS); //cancel if an action was taken
	}

	public static InteractionResult onLeftClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
		if (ConfigHandler.SERVER.inWorldUnReinforcing.get()) {
			if (PlayerUtils.isPlayerMountedOnCamera(player))
				return InteractionResult.FAIL;

			ItemStack stack = player.getMainHandItem();
			Item held = stack.getItem();

			if (held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()) {
				UniversalBlockReinforcerItem.maybeRemoveMending(level.registryAccess(), stack);

				if (UniversalBlockReinforcerItem.convertBlock(level.getBlockState(pos), level, stack, pos, player))
					return InteractionResult.FAIL; //When the client knows that a block will be converted on the server, it should not destroy that block (e.g. via instamining)
			}
		}

		return InteractionResult.PASS;
	}

	/**
	 * @return true if breaking the block should be canceled
	 */
	public static boolean onBlockEventBreak(Level level, Player player, BlockPos pos, BlockState state) {
		//don't let players in creative mode break the disguise block. it's not possible to break it in other gamemodes
		if (player.isCreative()) {
			List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(pos));

			if (!sentries.isEmpty())
				return true;
		}

		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof IModuleInventory be && be.shouldDropModules()) {
				for (int i = 0; i < be.getMaxNumberOfModules(); i++) {
					if (!be.getInventory().get(i).isEmpty()) {
						ItemStack stack = be.getInventory().get(i);
						ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);

						LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(item));
						be.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

						if (be instanceof LinkableBlockEntity lbe)
							lbe.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), lbe);

						if (be instanceof SecurityCameraBlockEntity cam) {
							BlockPos camPos = cam.getBlockPos();
							BlockState camState = level.getBlockState(camPos);

							level.updateNeighborsAt(camPos.relative(camState.getValue(SecurityCameraBlock.FACING), -1), camState.getBlock());
						}
					}
				}
			}

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log(player, DetectionMode.BREAK, pos, state));
		}

		return false;
	}

	public static void onBlockEventPlace(Level level, Entity entity, BlockPos pos, BlockState state) {
		if (level.isClientSide())
			return;

		if (entity instanceof Player player)
			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log(player, DetectionMode.PLACE, pos, state));
	}

	public static boolean onPlayerHarvestCheck(Level level, BlockPos pos, boolean canHarvest) {
		if (ConfigHandler.SERVER.alwaysDrop.get() && level.getBlockEntity(pos) instanceof IOwnable)
			return true;

		return canHarvest;
	}

	public static void onOwnership(OwnershipEvent event) {
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof IOwnable ownable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().id().toString();

			ownable.setOwner(uuid, name);
		}
	}

	/**
	 * @return true if setting the new target should be canceled
	 */
	public static boolean onLivingSetAttackTarget(Entity newTarget) {
		return newTarget instanceof Sentry;
	}

	/**
	 * @return true if the entity should not be able to destroy the block
	 */
	public static boolean onLivingDestroyEvent(Entity entity, BlockState state) {
		return entity instanceof WitherBoss && state.getBlock() instanceof IReinforcedBlock;
	}

	public static InteractionResult onRightClickItem(Player player, Level level, InteractionHand hand) {
		if (PlayerUtils.isPlayerMountedOnCamera(player) && player.getItemInHand(hand).getItem() != SCContent.CAMERA_MONITOR.get())
			return InteractionResult.FAIL;

		return InteractionResult.PASS;
	}

	public static void onFurnaceFuelBurnTime(FuelValues.Builder builder) {
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof BlockItem blockItem && (blockItem.getBlock() instanceof ReinforcedCarpetBlock || blockItem.getBlock() == SCContent.ELECTRIFIED_IRON_FENCE_GATE.get()))
				builder.remove(item);
		}
	}

	/**
	 * @return true if the anvil should not produce a result
	 */
	public static boolean onAnvilCraftPre(ItemStack stack, ItemStack book) {
		if (stack.getItem() instanceof UniversalBlockReinforcerItem)
			return book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet().stream().anyMatch(e -> e.is(Enchantments.MENDING));

		return false;
	}

	public static void onEntityTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		Level level = entity.level();
		List<RiftStabilizerBlockEntity> targetPosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getTarget());
		List<RiftStabilizerBlockEntity> sourcePosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getPrev());
		List<RiftStabilizerBlockEntity> blockEntities = new ArrayList<>();
		TeleportationType type = TeleportationType.getTypeFromEvent(event);
		RiftStabilizerBlockEntity riftStabilizer = null;
		boolean targetPosProhibited = false;

		blockEntities.addAll(targetPosBlockEntities);
		blockEntities.addAll(sourcePosBlockEntities);
		blockEntities = blockEntities.stream().distinct().sorted(Comparator.comparingDouble(b -> Math.min(b.getBlockPos().distToCenterSqr(event.getTarget()), b.getBlockPos().distToCenterSqr(event.getPrev())))).toList();

		for (RiftStabilizerBlockEntity be : blockEntities) {
			if (!be.isDisabled() && be.getFilter(type) && (!(entity instanceof Player player) || !(be.isOwnedBy(player) && be.ignoresOwner()) && !be.isAllowed(player))) {
				riftStabilizer = be;
				targetPosProhibited = be.getBlockPos().distToCenterSqr(event.getTarget()) < be.getBlockPos().distToCenterSqr(event.getPrev());
				break;
			}
		}

		if (riftStabilizer != null) {
			BlockPos pos = riftStabilizer.getBlockPos();
			Vec3 centerPos = new AABB(pos).getCenter();
			Vec3 from = targetPosProhibited ? event.getTarget() : event.getPrev();
			Vec3 distance = from.subtract(centerPos);

			if (entity instanceof Player player) {
				level.playSound(null, event.getPrevX(), event.getPrevY(), event.getPrevZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.5F);
				PlayerUtils.sendMessageToPlayer(player, SCContent.RIFT_STABILIZER.get().getName(), Component.translatable(targetPosProhibited ? "messages.securitycraft:rift_stabilizer.no_teleport_to" : "messages.securitycraft:rift_stabilizer.no_teleport_from"), ChatFormatting.RED);

				if (riftStabilizer.isModuleEnabled(ModuleType.HARMING))
					player.hurt(entity.damageSources().fall(), 5.0F);
			}

			riftStabilizer.setLastTeleport(Math.max(Math.abs(distance.x), Math.max(Math.abs(distance.y), Math.abs(distance.z))) - 0.5D, type);

			if (riftStabilizer.isModuleEnabled(ModuleType.REDSTONE)) {
				int signalLength = riftStabilizer.getSignalLength();

				level.setBlockAndUpdate(pos, riftStabilizer.getBlockState().cycle(RiftStabilizerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(level, pos, SCContent.RIFT_STABILIZER.get());

				if (signalLength > 0)
					level.scheduleTick(pos, SCContent.RIFT_STABILIZER.get(), signalLength);
			}

			event.setCanceled(true);
		}
	}

	/**
	 * Fires the teleport handler and returns whether the teleport should be canceled
	 */
	public static boolean isTeleportCanceled(EntityTeleportEvent event) {
		onEntityTeleport(event);
		return event.isCanceled();
	}

	public static void onNoteBlockPlayed(Level level, BlockPos pos, int vanillaNoteId, NoteBlockInstrument instrument) {
		handlePlayedNote(level, pos, vanillaNoteId, instrument, "");
	}

	private static void handlePlayedNote(Level level, BlockPos pos, int vanillaNoteId, NoteBlockInstrument instrument, String customSoundId) {
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = BlockEntityTracker.SONIC_SECURITY_SYSTEM.getBlockEntitiesInRange(level, pos);

		// If no custom sound id is given, check if a custom sound was played, and if so, store its id
		if (customSoundId.isEmpty() && instrument.hasCustomSound() && level.getBlockEntity(pos.above()) instanceof SkullBlockEntity be) {
			Identifier noteBlockSound = be.getNoteBlockSound();

			if (noteBlockSound != null)
				customSoundId = noteBlockSound.toString();
		}

		for (SonicSecuritySystemBlockEntity be : sonicSecuritySystems) {
			// If the SSS is disabled, don't listen to any notes
			if (!be.isActive())
				continue;

			// If the SSS is recording, record the note being played
			// Otherwise, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			if (be.isRecording())
				be.recordNote(vanillaNoteId, instrument, customSoundId);
			else
				be.listenToNote(vanillaNoteId, instrument, customSoundId);
		}
	}
}
