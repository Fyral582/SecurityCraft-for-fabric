package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.renderers.state.KeypadChestRenderState;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Allows the keypad chest to use its own textures. This replaces NeoForge's ChestRenderState#customSprite.
 */
@Mixin(ChestRenderer.class)
public class ChestRendererMixin {
	@WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;chooseSprite(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/sprite/SpriteId;"))
	private SpriteId securitycraft$useCustomMaterial(ChestRenderState.ChestMaterialType materialType, ChestType chestType, Operation<SpriteId> original, @Local(argsOnly = true) ChestRenderState state) {
		if (state instanceof KeypadChestRenderState keypadChestState && keypadChestState.customSprite != null)
			return keypadChestState.customSprite;

		return original.call(materialType, chestType);
	}
}
