package net.geforcemods.securitycraft.platform;

/**
 * Render state data that marks players that are holding a taser, see the taser mixins. This is implemented on
 * HumanoidRenderState by HumanoidRenderStateMixin, since Fabric API's render state data keys don't exist for all supported
 * Minecraft versions.
 */
public interface TaserRenderData {
	boolean securitycraft$holdsTaser();

	void securitycraft$setHoldsTaser(boolean holdsTaser);
}
