package lib.crescent.nms.world;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lib.crescent.Reflect;
import lib.crescent.nms.MappingsEntry;
import lib.crescent.nms.NMSManipulator;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;

public class LevelChunk {

	@SuppressWarnings({ "unchecked", "resource" })
	public static final Chunk[] getUpdatingChunks(WorldServer world) {
		PlayerChunkMap chunkMap = world.getChunkSource().chunkMap;
		// net.minecraft.server.level.ChunkMap.updatingChunkMap有可能不存在，一些服务端或补丁会篡改class文件，将区块托管到它们的系统中
		Field updatingChunkMap = Reflect.getField(chunkMap, MappingsEntry.getObfuscatedName("net.minecraft.server.level.ChunkMap.updatingChunkMap"));
		if (updatingChunkMap == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get updating chunks. Class file net.minecraft.server.level.ChunkMap has been modified by server/patches so loaded chunks are no longer stored in this class.");
			return null;
		}
		Long2ObjectLinkedOpenHashMap<PlayerChunk> loaded_chunk_map = (Long2ObjectLinkedOpenHashMap<PlayerChunk>) Reflect.getValue(chunkMap, updatingChunkMap);
		ArrayList<Chunk> updating_chunks = new ArrayList<>();
		loaded_chunk_map.forEach((Long l, PlayerChunk player_chunk) -> {
			updating_chunks.add(player_chunk.getFullChunkNow());
		});
		Chunk[] result = new Chunk[updating_chunks.size()];
		return updating_chunks.toArray(result);
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public static final Chunk[] getVisibleChunks(WorldServer world) {
		ArrayList<Chunk> visible_chunks = new ArrayList<>();
		Iterable<PlayerChunk> chunks = (Iterable<PlayerChunk>) NMSManipulator.invoke(world.getChunkSource().chunkMap, "net.minecraft.server.level.ChunkMap.getChunks()", null);
		for (PlayerChunk chunk : chunks)
			visible_chunks.add(chunk.getFullChunkNow());
		Chunk[] result = new Chunk[visible_chunks.size()];
		return visible_chunks.toArray(result);
	}
}
