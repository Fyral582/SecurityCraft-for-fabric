package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the security camera item, which uses the builtin/entity model in 1.21.2 and 1.21.3
 */
public class SecurityCameraItemRenderer implements DynamicItemRenderer {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/entity/security_camera/security_camera.png");
	private static final int LENS_COLOR = ARGB.color(255, 0x70FFFF);
	private SecurityCameraModel model;

	@Override
	public void render(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (model == null)
			model = new SecurityCameraModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));

		model.cameraRotationPoint2.visible = false;
		model.rotateCameraY(0.0F);
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, packedOverlay, LENS_COLOR);
		model.cameraRotationPoint2.visible = true;
	}
}
