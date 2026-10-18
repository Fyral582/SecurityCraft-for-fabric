package net.geforcemods.securitycraft;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.items.properties.HitCheck;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.Tips;
import net.geforcemods.securitycraft.platform.PlatformClientHooks;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class SecurityCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigRegistry.INSTANCE.register(SecurityCraft.MODID, ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		ConfigScreenFactoryRegistry.INSTANCE.register(SecurityCraft.MODID, ConfigurationScreen::new);
		HitCheck.bootstrap();
		PlatformClientHooks.init();
		KeyBindings.registerKeyMappings();
		ClientHandler.init();
		SCClientEventHandler.register();
		CameraController.register();
		FrameFeedHandler.register();
		Tips.register();
	}
}
