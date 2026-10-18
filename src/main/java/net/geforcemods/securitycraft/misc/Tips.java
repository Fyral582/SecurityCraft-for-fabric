package net.geforcemods.securitycraft.misc;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.platform.ChatLinks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Tips {
	public static final Map<String, String> TIPS_WITH_LINK = new HashMap<>();

	static {
		TIPS_WITH_LINK.put("patreon", "https://www.patreon.com/Geforce");
		TIPS_WITH_LINK.put("discord", "https://discord.gg/U8DvBAW");
		TIPS_WITH_LINK.put("outdated", "https://www.curseforge.com/minecraft/mc-mods/security-craft/files/all");
	}

	private Tips() {}

	public static void register() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, mc) -> onLoggingIn());
	}

	public static void onLoggingIn() {
		if (!ConfigHandler.SERVER.disableThanksMessage.get() && ConfigHandler.CLIENT.sayThanksMessage.get()) {
			//@formatter:off
			String tipKey = getRandomTip();
			MutableComponent message = Component.literal("[")
					.append(Component.literal("SecurityCraft").withStyle(ChatFormatting.GOLD))
					.append(Component.literal("] "))
					.append(Utils.localize("messages.securitycraft:thanks",
							SecurityCraft.getVersion(),
							Utils.localize("messages.securitycraft:tip"),
							Utils.localize(tipKey)));
			//@formatter:on

			if (TIPS_WITH_LINK.containsKey(tipKey.split("\\.")[2]))
				message = message.append(ChatLinks.newChatWithLinks(TIPS_WITH_LINK.get(tipKey.split("\\.")[2])));

			ClientHandler.getClientPlayer().displayClientMessage(message, false);
		}
	}

	private static String getRandomTip() {
		//@formatter:off
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver",
				"messages.securitycraft:tip.outdated"
		};
		//@formatter:on

		return tips[SecurityCraft.RANDOM.nextInt(isOutdated() ? tips.length : tips.length - 1)];
	}

	private static boolean isOutdated() {
		return false; //NeoForge's update checker is not available on Fabric
	}
}
