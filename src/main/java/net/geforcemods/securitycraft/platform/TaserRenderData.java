package net.geforcemods.securitycraft.platform;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

/**
 * Render state data that marks players that are holding a taser, see the taser mixins
 */
public final class TaserRenderData {
	public static final RenderStateDataKey<Boolean> HOLDS_TASER = RenderStateDataKey.create(() -> "securitycraft:holds_taser");

	private TaserRenderData() {}
}
