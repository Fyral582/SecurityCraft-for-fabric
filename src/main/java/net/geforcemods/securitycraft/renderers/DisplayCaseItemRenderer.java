package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the display case items, which use the builtin/entity model in 1.21.2 and 1.21.3
 */
public class DisplayCaseItemRenderer implements DynamicItemRenderer {
	private final ResourceLocation texture;
	private final boolean glowing;
	private DisplayCaseModel model;

	public DisplayCaseItemRenderer(boolean glowing) {
		this.glowing = glowing;
		texture = SecurityCraft.resLoc("textures/entity/display_case/" + (glowing ? "glow" : "normal") + ".png");
	}

	@Override
	public void render(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (model == null)
			model = new DisplayCaseModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientHandler.DISPLAY_CASE_LOCATION));

		model.setUpAnim(0.0F);
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entityCutout(texture)), glowing ? LightTexture.FULL_BRIGHT : packedLight, packedOverlay);
	}
}
