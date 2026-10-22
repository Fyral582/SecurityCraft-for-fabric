package net.geforcemods.securitycraft.platform.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.util.Mth;

/**
 * Fabric replacement for NeoForge's ScrollPanel: a scrollable area with a vertical scroll bar whose content is drawn by
 * subclasses.
 */
public abstract class ScrollPanel extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
	private final Minecraft client;
	protected final int width;
	protected final int height;
	protected final int top;
	protected final int bottom;
	protected final int right;
	protected final int left;
	private boolean scrolling;
	protected float scrollDistance;
	protected boolean captureMouse = true;
	protected final int border;
	private final int barWidth;
	private final int barLeft;
	private final int barBgColor;
	private final int barColor;
	private final int barBorderColor;

	public ScrollPanel(Minecraft client, int width, int height, int top, int left) {
		this(client, width, height, top, left, 4);
	}

	public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border) {
		this(client, width, height, top, left, border, 6);
	}

	public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth) {
		this(client, width, height, top, left, border, barWidth, 0xFF000000, 0xFF808080, 0xFFC0C0C0);
	}

	public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int barBgColor, int barColor, int barBorderColor) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.left = left;
		this.bottom = height + top;
		this.right = width + left;
		this.border = border;
		this.barWidth = barWidth;
		this.barLeft = left + width - barWidth;
		this.barBgColor = barBgColor;
		this.barColor = barColor;
		this.barBorderColor = barBorderColor;
	}

	protected abstract int getContentHeight();

	/**
	 * Draws the background of the scroll panel. By default, this draws a dark gradient like vanilla's lists.
	 */
	protected void drawBackground(GuiGraphics guiGraphics, float partialTick) {
		guiGraphics.fillGradient(left, top, right, bottom, 0xC0101010, 0xD0101010);
	}

	/**
	 * Draws the actual content of the scroll panel
	 *
	 * @param entryRight The x position of the right edge of the content area
	 * @param relativeY The y position the content starts at, taking scrolling into account
	 */
	protected abstract void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY);

	protected boolean clickPanel(double mouseX, double mouseY, int button) {
		return false;
	}

	private int getMaxScroll() {
		return getContentHeight() - (height - border);
	}

	private void applyScrollLimits() {
		int max = getMaxScroll();

		if (max < 0)
			max /= 2;

		if (scrollDistance < 0.0F)
			scrollDistance = 0.0F;

		if (scrollDistance > max)
			scrollDistance = max;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (scrollY != 0) {
			scrollDistance += (float) (-scrollY * getScrollAmount());
			applyScrollLimits();
			return true;
		}

		return false;
	}

	protected int getScrollAmount() {
		return 20;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= bottom;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button))
			return true;

		scrolling = button == 0 && mouseX >= barLeft && mouseX < barLeft + barWidth;

		if (scrolling)
			return true;

		int mouseListY = ((int) mouseY) - top - getContentHeight() + (int) scrollDistance - border;

		if (mouseX >= left && mouseX <= right && mouseListY < 0)
			return clickPanel(mouseX - left, mouseY - top + (int) scrollDistance - border, button);

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (super.mouseReleased(mouseX, mouseY, button))
			return true;

		boolean wasScrolling = scrolling;

		scrolling = false;
		return wasScrolling;
	}

	private int getBarHeight() {
		int barHeight = (height * height) / Math.max(1, getContentHeight());

		if (barHeight < 32)
			barHeight = 32;

		if (barHeight > height - border * 2)
			barHeight = height - border * 2;

		return barHeight;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (scrolling) {
			int maxScroll = height - getBarHeight();
			double moved = deltaY / maxScroll;

			scrollDistance += (float) (getMaxScroll() * moved);
			applyScrollLimits();
			return true;
		}

		return false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		drawBackground(guiGraphics, partialTick);
		guiGraphics.enableScissor(left, top, right, bottom);

		int baseY = top + border - (int) scrollDistance;

		drawPanel(guiGraphics, right, baseY, mouseX, mouseY);

		int extraHeight = getContentHeight() + border - height;

		if (extraHeight > 0) {
			int barHeight = getBarHeight();
			int barTop = (int) scrollDistance * (height - barHeight) / extraHeight + top;

			if (barTop < top)
				barTop = top;

			guiGraphics.fill(barLeft, top, barLeft + barWidth, bottom, barBgColor);
			guiGraphics.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, barColor);
			guiGraphics.fill(barLeft, barTop, barLeft + barWidth - 1, barTop + barHeight - 1, barBorderColor);
		}

		guiGraphics.disableScissor();
	}

	protected void drawGradientRect(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color1, int color2) {
		guiGraphics.fillGradient(left, top, right, bottom, color1, color2);
	}

	protected Minecraft getClient() {
		return client;
	}

	protected float clampScroll(float value) {
		return Mth.clamp(value, 0.0F, Math.max(0, getMaxScroll()));
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return Collections.emptyList();
	}
}
