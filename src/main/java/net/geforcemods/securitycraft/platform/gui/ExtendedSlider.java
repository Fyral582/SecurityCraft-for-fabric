package net.geforcemods.securitycraft.platform.gui;

import org.lwjgl.glfw.GLFW;
import java.text.DecimalFormat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Fabric replacement for NeoForge's ExtendedSlider: a slider with a minimum and maximum value, a step size and an optional
 * prefix and suffix for the displayed value.
 */
public class ExtendedSlider extends AbstractSliderButton {
	protected Component prefix;
	protected Component suffix;
	protected double minValue;
	protected double maxValue;
	protected double stepSize;
	protected boolean drawString;
	private final DecimalFormat format;
	private int fgColor = -1;

	public ExtendedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
		super(x, y, width, height, Component.empty(), 0.0D);
		this.prefix = prefix;
		this.suffix = suffix;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepSize = Math.abs(stepSize);
		this.drawString = drawString;
		value = snapToNearest((currentValue - minValue) / (maxValue - minValue));

		if (stepSize == 0.0D) {
			precision = Math.min(Math.max(precision, 0), 4);
			format = new DecimalFormat(precision == 0 ? "0" : "0." + "0".repeat(precision));
		}
		else {
			String step = Double.toString(this.stepSize);
			int decimals = step.endsWith(".0") ? 0 : step.length() - step.indexOf('.') - 1;

			format = new DecimalFormat(decimals == 0 ? "0" : "0." + "0".repeat(decimals));
		}

		updateMessage();
	}

	public ExtendedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
		this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0D, 0, drawString);
	}

	public double getValue() {
		return value * (maxValue - minValue) + minValue;
	}

	public long getValueLong() {
		return Math.round(getValue());
	}

	public int getValueInt() {
		return (int) getValueLong();
	}

	public void setValue(double value) {
		this.value = snapToNearest((value - minValue) / (maxValue - minValue));
		updateMessage();
	}

	public String getValueString() {
		return format.format(getValue());
	}

	public void setFGColor(int color) {
		fgColor = color;
	}

	public int getFGColor() {
		return fgColor;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		setSliderValue(mouseX);
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		super.onDrag(mouseX, mouseY, dragX, dragY);
		setSliderValue(mouseX);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean isLeft = keyCode == GLFW.GLFW_KEY_LEFT;

		if (isLeft || keyCode == GLFW.GLFW_KEY_RIGHT) {
			if (minValue > maxValue)
				setSliderValueInternal(value + (isLeft ? 1 : -1) * getStepFraction());
			else
				setSliderValueInternal(value + (isLeft ? -1 : 1) * getStepFraction());

			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private double getStepFraction() {
		double range = Math.abs(maxValue - minValue);

		return stepSize <= 0.0D || range == 0.0D ? 1.0D / Math.max(1, width - 8) : stepSize / range;
	}

	private void setSliderValue(double mouseX) {
		setSliderValueInternal((mouseX - (getX() + 4)) / (width - 8));
	}

	private void setSliderValueInternal(double newValue) {
		double oldValue = value;

		value = snapToNearest(newValue);

		if (!Mth.equal(oldValue, value))
			applyValue();

		updateMessage();
	}

	private double snapToNearest(double value) {
		if (stepSize <= 0.0D)
			return Mth.clamp(value, 0.0D, 1.0D);

		value = Mth.lerp(Mth.clamp(value, 0.0D, 1.0D), minValue, maxValue);
		value = stepSize * Math.round(value / stepSize);

		if (minValue > maxValue)
			value = Mth.clamp(value, maxValue, minValue);
		else
			value = Mth.clamp(value, minValue, maxValue);

		return Mth.map(value, minValue, maxValue, 0.0D, 1.0D);
	}

	@Override
	protected void updateMessage() {
		if (drawString)
			setMessage(Component.empty().append(prefix).append(getValueString()).append(suffix));
		else
			setMessage(Component.empty());
	}

	@Override
	protected void applyValue() {}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (fgColor != -1) {
			Component message = getMessage();

			setMessage(message.copy().withColor(fgColor & 0xFFFFFF));
			super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
			setMessage(message);
		}
		else
			super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
	}
}
