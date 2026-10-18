package net.geforcemods.securitycraft.mixin.platform;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.geforcemods.securitycraft.platform.menu.ExtendedMenuType;
import net.geforcemods.securitycraft.platform.menu.MenuDataPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Sends the additional data of SecurityCraft's menus to the client right before the menu is opened there
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMenuDataMixin {
	@Inject(method = "openMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
	private void securitycraft$sendMenuData(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir, @Local AbstractContainerMenu menu) {
		if (menu.getType() instanceof ExtendedMenuType<?>) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());

			try {
				provider.writeClientSideData(menu, buf);

				byte[] data = new byte[buf.readableBytes()];

				buf.readBytes(data);
				ServerPlayNetworking.send(player, new MenuDataPayload(menu.containerId, data));
			}
			finally {
				buf.release();
			}
		}
	}
}
