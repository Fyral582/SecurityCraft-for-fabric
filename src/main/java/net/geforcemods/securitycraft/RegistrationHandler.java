package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.List;

import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBarrelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedChiseledBookshelfBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.entity.AbstractSecuritySeaBoat;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.network.client.InteractWithFrame;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.network.client.SendManualPages;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.platform.network.IPayloadContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.geforcemods.securitycraft.platform.registry.DeferredBlock;
import net.geforcemods.securitycraft.platform.SCBrewingRecipes;
import net.geforcemods.securitycraft.platform.menu.MenuDataPayload;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockEntityApiProvider;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RegistrationHandler {
	private RegistrationHandler() {}

	public static void registerBlockItems() {
		{
			//register item blocks from annotated fields
			for (Field field : SCContent.class.getFields()) {
				try {
					if (field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem()) {
						SCItemGroup group = field.getAnnotation(Reinforced.class).itemGroup();
						DeferredBlock<Block> deferredBlock = (DeferredBlock<Block>) field.get(null);
						Block block = deferredBlock.get();
						Item blockItem = new BlockItem(block, SCContent.setId(deferredBlock.getKey().location().getPath(), new Item.Properties().fireResistant(), true));

						Registry.register(BuiltInRegistries.ITEM, Utils.getRegistryName(block), blockItem);

						if (group != SCItemGroup.MANUAL)
							SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
					}
					else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
						SCItemGroup group = field.getAnnotation(RegisterItemBlock.class).value();
						DeferredBlock<Block> deferredBlock = (DeferredBlock<Block>) field.get(null);
						Block block = deferredBlock.get();
						Item blockItem = new BlockItem(block, SCContent.setId(deferredBlock.getKey().location().getPath(), new Item.Properties(), true));

						Registry.register(BuiltInRegistries.ITEM, Utils.getRegistryName(block), blockItem);

						if (group != SCItemGroup.MANUAL)
							SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void registerSounds() {
		for (int i = 0; i < SCSounds.values().length; i++) {
			SCSounds sound = SCSounds.values()[i];

			Registry.register(BuiltInRegistries.SOUND_EVENT, sound.location, sound.event);
		}
	}

	public static void init() {
		FabricDefaultAttributeRegistry.register(SCContent.SENTRY_ENTITY.get(), Mob.createMobAttributes());
		onRegisterPayloadHandlers();
		onRegisterCapabilities();

		for (ResourceKey<CreativeModeTab> tabKey : List.of(CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.COLORED_BLOCKS, SCCreativeModeTabs.DECORATION_TAB.getKey(), CreativeModeTabs.OP_BLOCKS, CreativeModeTabs.TOOLS_AND_UTILITIES)) {
			ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> onCreativeModeTabRegister(tabKey, entries));
		}

		registerBrewingRecipes();
	}



	private static void onRegisterPayloadHandlers() {
		Registrar registrar = new Registrar();

		registrar.playToClient(MenuDataPayload.TYPE, MenuDataPayload.STREAM_CODEC);

		registrar.playToClient(BlockPocketManagerFailedActivation.TYPE, BlockPocketManagerFailedActivation.STREAM_CODEC);
		registrar.playToClient(InteractWithFrame.TYPE, InteractWithFrame.STREAM_CODEC);
		registrar.playToClient(OpenScreen.TYPE, OpenScreen.STREAM_CODEC);
		registrar.playToClient(PlayAlarmSound.TYPE, PlayAlarmSound.STREAM_CODEC);
		registrar.playToClient(RefreshDisguisableModel.TYPE, RefreshDisguisableModel.STREAM_CODEC);
		registrar.playToClient(SendManualPages.TYPE, SendManualPages.STREAM_CODEC);
		registrar.playToClient(SetCameraView.TYPE, SetCameraView.STREAM_CODEC);
		registrar.playToClient(SetTrophySystemTarget.TYPE, SetTrophySystemTarget.STREAM_CODEC);
		registrar.playToClient(UpdateLaserColors.TYPE, UpdateLaserColors.STREAM_CODEC);
		registrar.playToClient(UpdateLogger.TYPE, UpdateLogger.STREAM_CODEC);
		registrar.playToServer(AssembleBlockPocket.TYPE, AssembleBlockPocket.STREAM_CODEC, AssembleBlockPocket::handle);
		registrar.playToServer(CheckBriefcasePasscode.TYPE, CheckBriefcasePasscode.STREAM_CODEC, CheckBriefcasePasscode::handle);
		registrar.playToServer(CheckPasscode.TYPE, CheckPasscode.STREAM_CODEC, CheckPasscode::handle);
		registrar.playToServer(ClearChangeDetectorServer.TYPE, ClearChangeDetectorServer.STREAM_CODEC, ClearChangeDetectorServer::handle);
		registrar.playToServer(ClearLoggerServer.TYPE, ClearLoggerServer.STREAM_CODEC, ClearLoggerServer::handle);
		registrar.playToServer(DismountCamera.TYPE, DismountCamera.STREAM_CODEC, DismountCamera::handle);
		registrar.playToServer(MountCamera.TYPE, MountCamera.STREAM_CODEC, MountCamera::handle);
		registrar.playToServer(RemoteControlMine.TYPE, RemoteControlMine.STREAM_CODEC, RemoteControlMine::handle);
		registrar.playToServer(RemoveCameraTag.TYPE, RemoveCameraTag.STREAM_CODEC, RemoveCameraTag::handle);
		registrar.playToServer(RemoveMineFromMRAT.TYPE, RemoveMineFromMRAT.STREAM_CODEC, RemoveMineFromMRAT::handle);
		registrar.playToServer(RemovePositionFromSSS.TYPE, RemovePositionFromSSS.STREAM_CODEC, RemovePositionFromSSS::handle);
		registrar.playToServer(RemoveSentryFromSRAT.TYPE, RemoveSentryFromSRAT.STREAM_CODEC, RemoveSentryFromSRAT::handle);
		registrar.playToServer(SetBriefcasePasscodeAndOwner.TYPE, SetBriefcasePasscodeAndOwner.STREAM_CODEC, SetBriefcasePasscodeAndOwner::handle);
		registrar.playToServer(SetCameraPowered.TYPE, SetCameraPowered.STREAM_CODEC, SetCameraPowered::handle);
		registrar.playToServer(SetDefaultCameraViewingDirection.TYPE, SetDefaultCameraViewingDirection.STREAM_CODEC, SetDefaultCameraViewingDirection::handle);
		registrar.playToServer(SetGhostSlot.TYPE, SetGhostSlot.STREAM_CODEC, SetGhostSlot::handle);
		registrar.playToServer(SetKeycardUses.TYPE, SetKeycardUses.STREAM_CODEC, SetKeycardUses::handle);
		registrar.playToServer(SetListModuleData.TYPE, SetListModuleData.STREAM_CODEC, SetListModuleData::handle);
		registrar.playToServer(SetPasscode.TYPE, SetPasscode.STREAM_CODEC, SetPasscode::handle);
		registrar.playToServer(SetSentryMode.TYPE, SetSentryMode.STREAM_CODEC, SetSentryMode::handle);
		registrar.playToServer(SetStateOnDisguiseModule.TYPE, SetStateOnDisguiseModule.STREAM_CODEC, SetStateOnDisguiseModule::handle);
		registrar.playToServer(SyncAlarmSettings.TYPE, SyncAlarmSettings.STREAM_CODEC, SyncAlarmSettings::handle);
		registrar.playToServer(SyncBlockChangeDetector.TYPE, SyncBlockChangeDetector.STREAM_CODEC, SyncBlockChangeDetector::handle);
		registrar.playToServer(SyncBlockPocketManager.TYPE, SyncBlockPocketManager.STREAM_CODEC, SyncBlockPocketManager::handle);
		registrar.playToServer(SyncBlockReinforcer.TYPE, SyncBlockReinforcer.STREAM_CODEC, SyncBlockReinforcer::handle);
		registrar.playToServer(SyncFrame.TYPE, SyncFrame.STREAM_CODEC, SyncFrame::handle);
		registrar.playToServer(SyncKeycardSettings.TYPE, SyncKeycardSettings.STREAM_CODEC, SyncKeycardSettings::handle);
		registrar.playToServer(SyncLaserSideConfig.TYPE, SyncLaserSideConfig.STREAM_CODEC, SyncLaserSideConfig::handle);
		registrar.playToServer(SyncProjector.TYPE, SyncProjector.STREAM_CODEC, SyncProjector::handle);
		registrar.playToServer(SyncRiftStabilizer.TYPE, SyncRiftStabilizer.STREAM_CODEC, SyncRiftStabilizer::handle);
		registrar.playToServer(SyncSSSSettingsOnServer.TYPE, SyncSSSSettingsOnServer.STREAM_CODEC, SyncSSSSettingsOnServer::handle);
		registrar.playToServer(SyncSecureRedstoneInterface.TYPE, SyncSecureRedstoneInterface.STREAM_CODEC, SyncSecureRedstoneInterface::handle);
		registrar.playToServer(SyncTrophySystem.TYPE, SyncTrophySystem.STREAM_CODEC, SyncTrophySystem::handle);
		registrar.playToServer(ToggleBlockPocketManager.TYPE, ToggleBlockPocketManager.STREAM_CODEC, ToggleBlockPocketManager::handle);
		registrar.playToServer(ToggleModule.TYPE, ToggleModule.STREAM_CODEC, ToggleModule::handle);
		registrar.playToServer(ToggleNightVision.TYPE, ToggleNightVision.STREAM_CODEC, ToggleNightVision::handle);
		registrar.playToServer(ToggleOption.TYPE, ToggleOption.STREAM_CODEC, ToggleOption::handle);
		registrar.playToServer(UpdateSliderValue.TYPE, UpdateSliderValue.STREAM_CODEC, UpdateSliderValue::handle);
	}

	private static final Map<BlockEntityType<?>, BlockEntityApiProvider<Storage<ItemVariant>, Direction>> ITEM_STORAGE_PROVIDERS = new HashMap<>();

	/**
	 * Registers an item storage for the given block entity type. Block entity types that also support the reinforced moving
	 * piston (so they can be moved by pistons) would cause duplicate registrations for it, so a single provider that
	 * delegates based on the block entity type is registered for that block instead.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> void registerItemStorage(BiFunction<T, Direction, Storage<ItemVariant>> provider, BlockEntityType<T> type) {
		ITEM_STORAGE_PROVIDERS.put(type, (be, dir) -> provider.apply((T) be, dir));

		for (Block block : type.validBlocks) {
			if (block != SCContent.REINFORCED_MOVING_PISTON.get())
				ItemStorage.SIDED.registerForBlocks((level, pos, state, be, dir) -> be != null && be.getType() == type ? provider.apply((T) be, dir) : null, block);
		}
	}

	private static void onRegisterCapabilities() {
		registerItemStorage(AbstractKeypadFurnaceBlockEntity::getCapability, SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get());
		registerItemStorage(AbstractKeypadFurnaceBlockEntity::getCapability, SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get());
		registerItemStorage(AbstractKeypadFurnaceBlockEntity::getCapability, SCContent.KEYPAD_SMOKER_BLOCK_ENTITY.get());
		registerItemStorage(BlockPocketManagerBlockEntity::getCapability, SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get());
		registerItemStorage(ClaymoreBlockEntity::getCapability, SCContent.CLAYMORE_BLOCK_ENTITY.get());
		registerItemStorage(InventoryScannerBlockEntity::getCapability, SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get());
		registerItemStorage(KeypadBarrelBlockEntity::getCapability, SCContent.KEYPAD_BARREL_BLOCK_ENTITY.get());
		registerItemStorage((chest, dir) -> KeypadChestBlockEntity.getCapability((KeypadChestBlockEntity) chest, dir), SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get());
		registerItemStorage(LaserBlockBlockEntity::getCapability, SCContent.LASER_BLOCK_BLOCK_ENTITY.get());
		registerItemStorage(ReinforcedHopperBlockEntity::getCapability, SCContent.REINFORCED_HOPPER_BLOCK_ENTITY.get());
		registerItemStorage(TrophySystemBlockEntity::getCapability, SCContent.TROPHY_SYSTEM_BLOCK_ENTITY.get());
		registerItemStorage(ReinforcedChiseledBookshelfBlockEntity::getCapability, SCContent.REINFORCED_CHISELED_BOOKSHELF_BLOCK_ENTITY.get());
		registerItemStorage(ReinforcedDispenserBlockEntity::getCapability, SCContent.REINFORCED_DISPENSER_BLOCK_ENTITY.get());
		registerItemStorage(ReinforcedDropperBlockEntity::getCapability, SCContent.REINFORCED_DROPPER_BLOCK_ENTITY.get());
		registerItemStorage(SecurityCameraBlockEntity::getCapability, SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get());
		ItemStorage.SIDED.registerForBlocks((level, pos, state, be, dir) -> {
			BlockEntityApiProvider<Storage<ItemVariant>, Direction> provider = be == null ? null : ITEM_STORAGE_PROVIDERS.get(be.getType());

			return provider == null ? null : provider.find(be, dir);
		}, SCContent.REINFORCED_MOVING_PISTON.get());
		FluidStorage.ITEM.registerForItems((stack, context) -> new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(SCContent.FAKE_WATER.get()), FluidConstants.BUCKET), SCContent.FAKE_WATER_BUCKET.get());
		FluidStorage.ITEM.registerForItems((stack, context) -> new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(SCContent.FAKE_LAVA.get()), FluidConstants.BUCKET), SCContent.FAKE_LAVA_BUCKET.get());
	}

	private static void onCreativeModeTabRegister(ResourceKey<CreativeModeTab> tabKey, FabricItemGroupEntries event) {
		//@formatter:off
		if (tabKey.equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
			insertAfter(event, new ItemStack(Items.LEVER), new ItemStack(SCContent.REINFORCED_LEVER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.STONE_BUTTON), new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), new ItemStack(SCContent.PANIC_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.STONE_PRESSURE_PLATE), new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), new ItemStack(SCContent.REINFORCED_STONE_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.STICKY_PISTON), new ItemStack(SCContent.REINFORCED_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.REINFORCED_PISTON.get()), new ItemStack(SCContent.REINFORCED_STICKY_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.DROPPER), new ItemStack(SCContent.REINFORCED_DISPENSER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.REINFORCED_DISPENSER.get()), new ItemStack(SCContent.REINFORCED_DROPPER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.HOPPER), new ItemStack(SCContent.REINFORCED_HOPPER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.LECTERN), new ItemStack(SCContent.REINFORCED_LECTERN.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (tabKey.equals(CreativeModeTabs.COLORED_BLOCKS)) {
			//@formatter:off
			event.acceptAll(List.of(
					new ItemStack(SCContent.REINFORCED_WHITE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_RED_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIME_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_PINK_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_RED_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIME_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_PINK_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_RED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIME_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PINK_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_RED_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIME_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_PINK_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_RED_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIME_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PINK_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_TINTED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_RED_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIME_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_PINK_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get())));
			//@formatter:on
		}
		else if (tabKey.equals(SCCreativeModeTabs.DECORATION_TAB.getKey())) {
			try {
				insertAfter(event, new ItemStack(SCContent.REINFORCED_LADDER.get()), new ItemStack(SCContent.REINFORCED_SCAFFOLDING_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
				insertAfter(event, new ItemStack(SCContent.REINFORCED_LECTERN.get()), new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			}
			catch (IllegalArgumentException e) {
				event.accept(new ItemStack(SCContent.REINFORCED_SCAFFOLDING_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
				event.accept(new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			}

			insertAfter(event, new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_PALE_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_PALE_OAK_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_PALE_OAK_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_PALE_OAK_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (tabKey.equals(CreativeModeTabs.OP_BLOCKS)) {
			if (event.shouldShowOpRestrictedItems()) {
				event.accept(new ItemStack(SCContent.ADMIN_TOOL.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
				event.accept(new ItemStack(SCContent.CODEBREAKER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			}
		}
		else if (tabKey.equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
			insertAfter(event, new ItemStack(Items.OAK_CHEST_BOAT), new ItemStack(SCContent.OAK_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.SPRUCE_CHEST_BOAT), new ItemStack(SCContent.SPRUCE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.BIRCH_CHEST_BOAT), new ItemStack(SCContent.BIRCH_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.JUNGLE_CHEST_BOAT), new ItemStack(SCContent.JUNGLE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.ACACIA_CHEST_BOAT), new ItemStack(SCContent.ACACIA_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.DARK_OAK_CHEST_BOAT), new ItemStack(SCContent.DARK_OAK_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.MANGROVE_CHEST_BOAT), new ItemStack(SCContent.MANGROVE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.CHERRY_CHEST_BOAT), new ItemStack(SCContent.CHERRY_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.PALE_OAK_CHEST_BOAT), new ItemStack(SCContent.PALE_OAK_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			insertAfter(event, new ItemStack(Items.BAMBOO_CHEST_RAFT), new ItemStack(SCContent.BAMBOO_SECURITY_SEA_RAFT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}

	private static void insertAfter(FabricItemGroupEntries entries, ItemStack after, ItemStack stack, TabVisibility visibility) {
		entries.addAfter(after, List.of(stack), visibility);
	}

	public static void registerBrewingRecipes() {
		SCBrewingRecipes.addRecipe(stack -> stack.is(Items.WATER_BUCKET), getPotionIngredient(Potions.HARMING, Potions.STRONG_HARMING), () -> new ItemStack(SCContent.FAKE_WATER_BUCKET.get()));
		SCBrewingRecipes.addRecipe(stack -> stack.is(Items.LAVA_BUCKET), getPotionIngredient(Potions.HEALING, Potions.STRONG_HEALING), () -> new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()));
	}

	private static java.util.function.Predicate<ItemStack> getPotionIngredient(Holder<Potion> normalPotion, Holder<Potion> strongPotion) {
		return stack -> (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().map(p -> p.is(normalPotion) || p.is(strongPotion)).orElse(false);
	}

	/**
	 * Mimics NeoForge's PayloadRegistrar on top of Fabric's networking API
	 */
	private static class Registrar {
		<T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
			PayloadTypeRegistry.playS2C().register(type, codec);
		}

		<T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, IPayloadContext> handler) {
			PayloadTypeRegistry.playC2S().register(type, codec);
			ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, context::player));
		}
	}
}
