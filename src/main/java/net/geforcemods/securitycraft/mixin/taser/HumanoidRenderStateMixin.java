package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.geforcemods.securitycraft.platform.TaserRenderData;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Stores whether a player is holding a taser, see {@link TaserRenderData}
 */
@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements TaserRenderData {
	@Unique
	private boolean securitycraft$holdsTaser;

	@Override
	public boolean securitycraft$holdsTaser() {
		return securitycraft$holdsTaser;
	}

	@Override
	public void securitycraft$setHoldsTaser(boolean holdsTaser) {
		securitycraft$holdsTaser = holdsTaser;
	}
}
