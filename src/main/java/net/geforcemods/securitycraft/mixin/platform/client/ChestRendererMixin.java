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
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Allows the keypad chest to use its own textures. This replaces NeoForge's ChestRenderState#customMaterial.
 */
@Mixin(ChestRenderer.class)
public class ChestRendererMixin {
	@WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;chooseMaterial(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/Material;"))
	private Material securitycraft$useCustomMaterial(ChestRenderState.ChestMaterialType materialType, ChestType chestType, Operation<Material> original, @Local(argsOnly = true) ChestRenderState state) {
		if (state instanceof KeypadChestRenderState keypadChestState && keypadChestState.customMaterial != null)
			return keypadChestState.customMaterial;

		return original.call(materialType, chestType);
	}
}
