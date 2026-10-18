package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.geforcemods.securitycraft.platform.ext.SCAbstractWidgetExtension;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Adds NeoForge's foreground color to widgets
 */
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements SCAbstractWidgetExtension {
	@Shadow
	public boolean active;
	@Shadow
	protected float alpha;
	@Unique
	private int securitycraft$fgColor = UNSET_FG_COLOR;

	@Override
	public int getFGColor() {
		int color = securitycraft$fgColor != UNSET_FG_COLOR ? securitycraft$fgColor : active ? 0xFFFFFF : 0xA0A0A0;

		return ARGB.color(Mth.ceil(alpha * 255.0F), color);
	}

	@Override
	public void setFGColor(int color) {
		securitycraft$fgColor = color;
	}
}
