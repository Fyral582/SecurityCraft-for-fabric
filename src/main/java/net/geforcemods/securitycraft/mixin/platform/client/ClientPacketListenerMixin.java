package net.geforcemods.securitycraft.mixin.platform.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

/**
 * Recreates NeoForge's IBlockEntityExtension#onDataPacket hook
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@WrapOperation(method = "method_38542", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
	private void securitycraft$onDataPacket(BlockEntity be, CompoundTag tag, HolderLookup.Provider lookupProvider, Operation<Void> original, @Local(argsOnly = true) ClientboundBlockEntityDataPacket packet) {
		be.onDataPacket(((ClientPacketListener) (Object) this).getConnection(), packet, lookupProvider);
	}
}
