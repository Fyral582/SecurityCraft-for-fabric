package net.geforcemods.securitycraft.platform.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Fabric replacement for NeoForge's ClientPacketDistributor
 */
public final class ClientPacketDistributor {
	private ClientPacketDistributor() {}

	public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
		send(payload);

		for (CustomPacketPayload p : payloads) {
			send(p);
		}
	}

	private static void send(CustomPacketPayload payload) {
		if (ClientPlayNetworking.canSend(payload.type()))
			ClientPlayNetworking.send(payload);
	}
}
