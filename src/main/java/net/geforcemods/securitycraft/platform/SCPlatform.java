package net.geforcemods.securitycraft.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.server.MinecraftServer;

/**
 * Replaces various NeoForge/FML helpers (ServerLifecycleHooks, ModList, FMLEnvironment, EffectiveSide)
 */
public final class SCPlatform {
	private static MinecraftServer currentServer;

	private SCPlatform() {}

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer = null);
	}

	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}

	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static boolean isProduction() {
		return !FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static boolean isPhysicalClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	/**
	 * @return true if the current thread is the logical client, false if it's the logical server
	 */
	public static boolean isLogicalClient() {
		if (!isPhysicalClient())
			return false;

		MinecraftServer server = currentServer;

		return server == null || !server.isSameThread();
	}

	public static String getModVersion(String modid) {
		return FabricLoader.getInstance().getModContainer(modid).map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
	}

	public static String getModName(String modid) {
		return FabricLoader.getInstance().getModContainer(modid).map(c -> c.getMetadata().getName()).orElse(modid);
	}

	public static String getVersion() {
		return getModVersion(SecurityCraft.MODID);
	}
}
