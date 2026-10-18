package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Recreates NeoForge's IBlockEntityExtension#onDataPacket hook
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@WrapOperation(method = "method_38542", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/world/level/storage/ValueInput;)V"))
	private void securitycraft$onDataPacket(BlockEntity be, ValueInput input, Operation<Void> original) {
		be.onDataPacket(((ClientPacketListener) (Object) this).getConnection(), input);
	}
}
