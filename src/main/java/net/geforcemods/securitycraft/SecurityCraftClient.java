package net.geforcemods.securitycraft;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.Tips;
import net.geforcemods.securitycraft.platform.PlatformClientHooks;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class SecurityCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NeoForgeConfigRegistry.INSTANCE.register(SecurityCraft.MODID, ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		ConfigScreenFactoryRegistry.INSTANCE.register(SecurityCraft.MODID, ConfigurationScreen::new);
		PlatformClientHooks.init();
		KeyBindings.registerKeyMappings();
		ClientHandler.init();
		SCClientEventHandler.register();
		CameraController.register();
		FrameFeedHandler.register();
		Tips.register();

		//development only: loads all mixin targets to find mixins that fail to apply, see the runClient configuration in build.gradle
		if (Boolean.getBoolean("securitycraft.mixinAudit"))
			ClientLifecycleEvents.CLIENT_STARTED.register(mc -> MixinEnvironment.getCurrentEnvironment().audit());
	}
}
