package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Allows the keypad chest to use its own textures. This replaces NeoForge's ChestRenderer#getMaterial.
 */
@Mixin(ChestRenderer.class)
public class ChestRendererMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;"))
	private Material securitycraft$useCustomMaterial(BlockEntity be, ChestType chestType, boolean holiday, Operation<Material> original) {
		if ((Object) this instanceof KeypadChestRenderer renderer && be instanceof ChestBlockEntity chest)
			return renderer.getMaterial(chest, chestType);

		return original.call(be, chestType, holiday);
	}
}
