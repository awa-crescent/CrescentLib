package lib.crescent.packet;

import java.util.List;

import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lib.crescent.nms.NMSManipulator;
import lib.crescent.nms.ServerEntry;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerConnection;

public abstract class ClientConnection {
	private static final ServerConnection server_connection;// ServerConnectionListener
	@SuppressWarnings("unused")
	private static final List<NetworkManager> client_connections;// List<Connection>

	static {
		server_connection = (ServerConnection) NMSManipulator.getObjectValue(ServerEntry.server, "net.minecraft.server.MinecraftServer.connection");
		client_connections = server_connection.getConnections();
	}

	/**
	 * 获取玩家对应的连接ServerGamePacketListenerImpl，可用于发包
	 * 
	 * @param player 玩家实体
	 * @return 玩家的连接
	 */
	public static PlayerConnection getServerGamePacketListenerImpl(Player player) {
		return (PlayerConnection) ((CraftPlayer) player).getHandle().connection;
	}

	/**
	 * 为某个玩家发送packet
	 * 
	 * @param player
	 * @param packet
	 */
	public static void sendPacket(Player player, net.minecraft.network.protocol.Packet<?> packet) {
		getServerGamePacketListenerImpl(player).send(packet);
	}

	public static void sendPacket(EntityPlayer player, net.minecraft.network.protocol.Packet<?> packet) {
		player.connection.send(packet);
	}

	/**
	 * 广播（为所有玩家发送）packet
	 * 
	 * @param packet
	 */
	public static void boardcastAll(net.minecraft.network.protocol.Packet<?> packet) {
		ServerEntry.server.getPlayerList().broadcastAll(packet);
	}
}
