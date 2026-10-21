package net.geforcemods.securitycraft.platform.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/**
 * Replacement for NeoForge's GUI layers (Minecraft#pushGuiLayer and Minecraft#popGuiLayer). Pushed screens are shown on top
 * of the previous screen, which keeps being rendered (but not ticked or interacted with) below it.
 */
public final class ScreenLayers {
	private static final Deque<Screen> LAYERS = new ArrayDeque<>();
	private static boolean changingLayers = false;

	private ScreenLayers() {}

	public static void pushGuiLayer(Screen screen) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.screen == null) {
			mc.setScreen(screen);
			return;
		}

		LAYERS.push(mc.screen);
		mc.screen = screen;
		screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
		mc.getNarrator().saySystemNow(screen.getNarrationMessage());
	}

	public static void popGuiLayer() {
		Minecraft mc = Minecraft.getInstance();

		if (LAYERS.isEmpty()) {
			mc.setScreen(null);
			return;
		}

		if (mc.screen != null)
			mc.screen.removed();

		mc.screen = LAYERS.pop();
	}

	/**
	 * Called when a screen is set the regular way, which closes all layers
	 */
	public static void onSetScreen() {
		if (!changingLayers) {
			changingLayers = true;

			try {
				for (Screen layer : LAYERS) {
					layer.removed();
				}

				LAYERS.clear();
			}
			finally {
				changingLayers = false;
			}
		}
	}

	public static void renderLayers(GuiGraphics guiGraphics, float partialTick) {
		Iterator<Screen> iterator = LAYERS.descendingIterator();

		while (iterator.hasNext()) {
			iterator.next().renderWithTooltip(guiGraphics, -1, -1, partialTick);
		}
	}

	public static void resize(int width, int height) {
		for (Screen layer : LAYERS) {
			layer.resize(Minecraft.getInstance(), width, height);
		}
	}
}
