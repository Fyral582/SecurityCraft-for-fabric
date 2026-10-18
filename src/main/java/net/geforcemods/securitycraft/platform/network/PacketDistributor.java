package net.geforcemods.securitycraft.platform.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.geforcemods.securitycraft.platform.SCPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

/**
 * Fabric replacement for NeoForge's PacketDistributor
 */
public final class PacketDistributor {
	private PacketDistributor() {}

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		send(player, payload);

		for (CustomPacketPayload p : payloads) {
			send(player, p);
		}
	}

	public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
		MinecraftServer server = SCPlatform.getCurrentServer();

		if (server != null) {
			for (ServerPlayer player : PlayerLookup.all(server)) {
				sendToPlayer(player, payload, payloads);
			}
		}
	}

	public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		for (ServerPlayer player : PlayerLookup.world(level)) {
			sendToPlayer(player, payload, payloads);
		}
	}

	public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		for (ServerPlayer player : PlayerLookup.tracking(level, chunkPos)) {
			sendToPlayer(player, payload, payloads);
		}
	}

	public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			sendToPlayer(player, payload, payloads);
		}
	}

	public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		sendToPlayersTrackingEntity(entity, payload, payloads);

		if (entity instanceof ServerPlayer player)
			sendToPlayer(player, payload, payloads);
	}

	private static void send(ServerPlayer player, CustomPacketPayload payload) {
		if (ServerPlayNetworking.canSend(player, payload.type()))
			ServerPlayNetworking.send(player, payload);
	}
}
