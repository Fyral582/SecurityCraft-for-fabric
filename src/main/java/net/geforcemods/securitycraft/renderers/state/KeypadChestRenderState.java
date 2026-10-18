package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;

public class KeypadChestRenderState extends ChestRenderState {
	public BlockEntityRenderState disguiseRenderState;
	public boolean isDisguised;
	/**
	 * Replaces NeoForge's ChestRenderState#customMaterial, used by ChestRendererMixin
	 */
	public Material customMaterial;
}
