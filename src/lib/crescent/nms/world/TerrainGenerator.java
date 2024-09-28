package lib.crescent.nms.world;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;

import lib.crescent.nms.NMSManipulator;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.dimension.WorldDimension;

public class TerrainGenerator {
	/*
	 * public static final net.minecraft.world.level.chunk.ChunkGenerator
	 * toNMSChunkGenerator(World world, ChunkGenerator generator) { WorldServer
	 * world_server = ((CraftWorld) world).getHandle(); return new
	 * CustomChunkGenerator(world_server, new
	 * net.minecraft.world.level.chunk.ChunkGenerator() { }, generator); }
	 */
	public static final void setChunkGenerator(WorldDimension stem, net.minecraft.world.level.chunk.ChunkGenerator generator) {
		NMSManipulator.setObject(stem, "net.minecraft.world.level.dimension.LevelStem.generator", generator);
	}

	public static final void setChunkGenerator(LevelStem.Type dim_type, net.minecraft.world.level.chunk.ChunkGenerator generator) {
		setChunkGenerator(LevelStem.getLevelStem(dim_type), generator);
	}

	@SuppressWarnings("resource")
	public static final void setChunkGenerator(WorldServer world, net.minecraft.world.level.chunk.ChunkGenerator generator) {
		PlayerChunkMap chunk_map = world.getChunkSource().chunkMap;
		NMSManipulator.setObject(NMSManipulator.access(chunk_map, "net.minecraft.server.level.ChunkMap.worldGenContext"), "net.minecraft.world.level.chunk.status.WorldGenContext.generator", generator);
	}

	public static final void setChunkGenerator(World world, net.minecraft.world.level.chunk.ChunkGenerator generator) {
		setChunkGenerator(((CraftWorld) world).getHandle(), generator);
	}
}
