package net.geforcemods.securitycraft.platform;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Replacement for NeoForge's CommonHooks#newChatWithLinks: creates a component in which all URLs are clickable
 */
public final class ChatLinks {
	private static final Pattern URL_PATTERN = Pattern.compile("(?:https?://)?(?:[-\\w_.]{2,}\\.[a-z]{2,4})(?:/\\S*)?", Pattern.CASE_INSENSITIVE);

	private ChatLinks() {}

	public static MutableComponent newChatWithLinks(String text) {
		MutableComponent result = Component.empty();
		Matcher matcher = URL_PATTERN.matcher(text);
		int lastEnd = 0;

		while (matcher.find()) {
			String url = matcher.group();

			if (matcher.start() > lastEnd)
				result.append(Component.literal(text.substring(lastEnd, matcher.start())));

			try {
				URI uri = new URI(url.startsWith("http") ? url : "https://" + url);

				result.append(Component.literal(url).withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(uri)).withUnderlined(true).withColor(ChatFormatting.BLUE)));
			}
			catch (Exception e) {
				result.append(Component.literal(url));
			}

			lastEnd = matcher.end();
		}

		if (lastEnd < text.length())
			result.append(Component.literal(text.substring(lastEnd)));

		return result;
	}
}
