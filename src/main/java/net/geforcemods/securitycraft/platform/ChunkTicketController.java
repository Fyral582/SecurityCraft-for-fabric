package net.geforcemods.securitycraft.platform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Fabric replacement for NeoForge's TicketController. Keeps track of which owners (block positions or entities) force-load
 * which chunks, and adds/removes a non-persistent vanilla ticket accordingly. Since the tickets are not persisted, no validation
 * of stale tickets is necessary when the level loads.
 */
public class ChunkTicketController {
	private final ResourceLocation id;
	private TicketType loadingTicket;
	private TicketType tickingTicket;
	private final Map<ResourceKey<Level>, Map<Long, Set<Object>>> forcedChunks = new HashMap<>();

	public ChunkTicketController(ResourceLocation id) {
		this.id = id;
	}

	public void register() {
		loadingTicket = Registry.register(BuiltInRegistries.TICKET_TYPE, id, new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING | TicketType.FLAG_KEEP_DIMENSION_ACTIVE));
		tickingTicket = Registry.register(BuiltInRegistries.TICKET_TYPE, id.withSuffix("_ticking"), new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION | TicketType.FLAG_KEEP_DIMENSION_ACTIVE));
	}

	public boolean forceChunk(ServerLevel level, BlockPos owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
		return forceChunkInternal(level, owner.immutable(), chunkX, chunkZ, add, ticking);
	}

	public boolean forceChunk(ServerLevel level, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
		return forceChunkInternal(level, owner.getUUID(), chunkX, chunkZ, add, ticking);
	}

	private synchronized boolean forceChunkInternal(ServerLevel level, Object owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
		ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
		Map<Long, Set<Object>> chunks = forcedChunks.computeIfAbsent(level.dimension(), k -> new HashMap<>());
		long key = chunkPos.toLong();
		TicketType type = ticking ? tickingTicket : loadingTicket;

		if (add) {
			Set<Object> owners = chunks.computeIfAbsent(key, k -> new HashSet<>());
			boolean wasEmpty = owners.isEmpty();
			boolean changed = owners.add(owner);

			if (wasEmpty && changed)
				level.getChunkSource().addTicketWithRadius(type, chunkPos, ticking ? 2 : 0);

			return changed;
		}
		else {
			Set<Object> owners = chunks.get(key);

			if (owners == null || !owners.remove(owner))
				return false;

			if (owners.isEmpty()) {
				chunks.remove(key);
				level.getChunkSource().removeTicketWithRadius(type, chunkPos, ticking ? 2 : 0);
			}

			return true;
		}
	}

	public synchronized void clear(ResourceKey<Level> level) {
		forcedChunks.remove(level);
	}
}
