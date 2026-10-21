package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock.IReinforcedCauldronInteraction;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.distanthorizons.DistantHorizonsCompat;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ConfigAttackTargetCheck;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.platform.ChunkTicketController;
import net.geforcemods.securitycraft.platform.PlatformHooks;
import net.geforcemods.securitycraft.platform.SCPlatform;
import net.geforcemods.securitycraft.platform.registry.DeferredBlock;
import net.geforcemods.securitycraft.platform.registry.DeferredHolder;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.config.ModConfig;

public class SecurityCraft implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "securitycraft";
	public static final Supplier<GameRules.Key<GameRules.BooleanValue>> RULE_FAKE_WATER_SOURCE_CONVERSION = Suppliers.memoize(() -> GameRules.register("fakeWaterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)));
	public static final Supplier<GameRules.Key<GameRules.BooleanValue>> RULE_FAKE_LAVA_SOURCE_CONVERSION = Suppliers.memoize(() -> GameRules.register("fakeLavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false)));
	public static final Random RANDOM = new Random();
	//tickets are not persisted, so there is no need to validate them when the level loads
	public static final ChunkTicketController CAMERA_TICKET_CONTROLLER = new ChunkTicketController(resLoc("camera_chunks"));

	@Override
	public void onInitialize() {
		SCPlatform.init();
		PlatformHooks.init();
		ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, ConfigHandler.SERVER_SPEC);
		ConfigHandler.registerEvents();
		CAMERA_TICKET_CONTROLLER.register();
		//same order as vanilla's/NeoForge's registries, so that entries can reference entries of registries that come before them
		RegistrationHandler.registerSounds();
		SCContent.DATA_COMPONENTS.register();
		SCContent.DATA_SERIALIZERS.register();
		RULE_FAKE_WATER_SOURCE_CONVERSION.get();
		RULE_FAKE_LAVA_SOURCE_CONVERSION.get();
		SCContent.PARTICLE_TYPES.register();
		SCContent.RECIPE_SERIALIZERS.register();
		SCContent.LOOT_ITEM_CONDITION_TYPES.register();
		SCContent.COMMAND_ARGUMENT_TYPES.register();
		SCContent.FLUIDS.register();
		SCContent.BLOCKS.register();
		SCContent.ENTITY_TYPES.register();
		SCContent.ITEMS.register();
		RegistrationHandler.registerBlockItems();
		SCContent.BLOCK_ENTITY_TYPES.register();
		SCContent.MENU_TYPES.register();
		SCCreativeModeTabs.CREATIVE_MODE_TABS.register();
		RegistrationHandler.init();
		SCEventHandler.register();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SCCommand.register(dispatcher));
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return resLoc("collect_sc_content_data");
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				collectSCContentData(true);
			}
		});
		ServerLifecycleEvents.SERVER_STARTED.register(server -> collectSCContentData(server, true));
		registerApiContent();
		SecurityCraftAPI.finishRegistration();

		if (SCPlatform.isModLoaded("distanthorizons"))
			DistantHorizonsCompat.registerEvent();

		IReinforcedCauldronInteraction.bootStrap();
	}

	private static void registerApiContent() {
		SecurityCraftAPI.registerExtractionBlock(ReinforcedHopperBlock.ExtractionBlock::new);
		SecurityCraftAPI.registerExtractionBlock(IMSBlock.ExtractionBlock::new);
		SecurityCraftAPI.registerPasscodeConvertible(KeypadBlock.Convertible::new);
		SecurityCraftAPI.registerPasscodeConvertible(KeypadBarrelBlock.Convertible::new);
		SecurityCraftAPI.registerPasscodeConvertible(KeypadChestBlock.Convertible::new);
		SecurityCraftAPI.registerPasscodeConvertible(KeypadTrapDoorBlock.Convertible::new);
		SecurityCraftAPI.registerPasscodeConvertible(() -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.FURNACE, SCContent.KEYPAD_FURNACE.get()));
		SecurityCraftAPI.registerPasscodeConvertible(() -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.SMOKER, SCContent.KEYPAD_SMOKER.get()));
		SecurityCraftAPI.registerPasscodeConvertible(() -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.BLAST_FURNACE, SCContent.KEYPAD_BLAST_FURNACE.get()));
		SecurityCraftAPI.registerSentryAttackTargetCheck(ConfigAttackTargetCheck::new);
		SecurityCraftAPI.registerDoorActivator(CommonDoorActivator::new);
		SecurityCraftAPI.registerDoorActivator(InventoryScannerBlock.DoorActivator::new);
		SecurityCraftAPI.registerDoorActivator(ReinforcedPressurePlateBlock.DoorActivator::new);
		SecurityCraftAPI.registerDoorActivator(ReinforcedRedstoneBlock.DoorActivator::new);
		SecurityCraftAPI.registerDoorActivator(SecureRedstoneInterfaceBlock.DoorActivator::new);
	}

	public static void collectSCContentData(boolean addPages) {
		collectSCContentData(SCPlatform.getCurrentServer(), addPages);
	}

	@SuppressWarnings("unchecked")
	public static void collectSCContentData(MinecraftServer server, boolean addPages) {
		if (addPages && server == null)
			return;

		Map<PageGroup, List<ItemStack>> groupStacks = new EnumMap<>(PageGroup.class);

		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.clear();
		IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.clear();

		if (addPages)
			SCManualItem.PAGES.clear();

		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class)) {
					Block block = ((DeferredBlock<Block>) field.get(null)).get();
					IReinforcedBlock rb = (IReinforcedBlock) block;

					IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(rb.getVanillaBlock(), block);
					IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block, rb.getVanillaBlock());
				}

				if (field.isAnnotationPresent(HasManualPage.class)) {
					Object o = ((DeferredHolder<?, ?>) field.get(null)).get();
					HasManualPage hmp = field.getAnnotation(HasManualPage.class);
					Item item = ((ItemLike) o).asItem();
					PageGroup group = hmp.value();
					boolean wasNotAdded = false;
					Component title = Component.translatable("");
					String key = "help.";

					if (group != PageGroup.NONE) {
						if (!groupStacks.containsKey(group)) {
							groupStacks.put(group, new ArrayList<>());
							title = Utils.localize(group.getTitle());
							key += group.getSpecialInfoKey();
							wasNotAdded = true;
						}

						groupStacks.get(group).add(new ItemStack(item));
					}
					else {
						title = Utils.localize(item.getDescriptionId());
						key += item.getDescriptionId().substring(5) + ".info";
					}

					if (addPages && (group == PageGroup.NONE || wasNotAdded))
						SCManualItem.PAGES.add(new SCManualPage(item, group, title, Component.translatable(key.replace("..", ".")), hmp.designedBy(), hmp.hasRecipeDescription(), Suppliers.memoize(() -> SCManualItem.findRecipes(server, item, group))));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		groupStacks.forEach((group, list) -> group.setItems(list.stream().filter(stack -> !stack.isEmpty()).toList()));
	}

	public static String getVersion() {
		return "v" + SCPlatform.getVersion();
	}

	public static ResourceLocation resLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static ResourceLocation mcResLoc(String path) {
		return ResourceLocation.withDefaultNamespace(path);
	}
}
