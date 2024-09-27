package lib.crescent.packet;

import java.util.List;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.entity.Player;

import lib.crescent.Reflect;
import lib.crescent.nms.world.LevelChunk;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;

public class Packet {
	public static final Class<?> ServerCommonPacketListenerImpl;

	static {
		ServerCommonPacketListenerImpl = Reflect.getClassForName("net.minecraft.server.network.ServerCommonPacketListenerImpl");
	}

	public static void updateChunk(EntityPlayer player, net.minecraft.world.level.chunk.Chunk chunk) {
		ClientConnection.sendPacket(player, new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null));
	}

	public static void updateChunk(Player player, net.minecraft.world.level.chunk.Chunk chunk) {
		ClientConnection.sendPacket(player, new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null));
	}

	public static void updateChunks(EntityPlayer player, net.minecraft.world.level.chunk.Chunk... chunks) {
		for (net.minecraft.world.level.chunk.Chunk chunk : chunks)
			ClientConnection.sendPacket(player, new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null));
	}

	public static void updateChunks(Player player, net.minecraft.world.level.chunk.Chunk... chunks) {
		for (net.minecraft.world.level.chunk.Chunk chunk : chunks)
			ClientConnection.sendPacket(player, new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null));
	}

	public static void updateChunks(List<EntityPlayer> players, net.minecraft.world.level.chunk.Chunk... chunks) {
		for (EntityPlayer player : players)
			for (net.minecraft.world.level.chunk.Chunk chunk : chunks)
				ClientConnection.sendPacket(player, new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null));
	}

	public static void updateAllChunks(WorldServer world) {
		updateChunks(world.players(), LevelChunk.getVisibleChunks(world));
	}

	public static void updateAllChunks(World world) {
		updateAllChunks(((CraftWorld) world).getHandle());
	}
}
