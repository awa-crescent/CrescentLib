package lib.crescent.packet;

import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.serialization.DynamicOps;

import lib.crescent.nms.ServerEntry;
import lib.crescent.nms.world.DimensionType;
import lib.crescent.nms.world.LevelChunk;
import lib.crescent.nms.world.LevelStem;
import lib.lunar.nativemc.NMSManipulator;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;//ClientboundRespawnPacket
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.phys.Vec3D;

public class Packets {

	/**
	 * 向客户端推送注册表内容更新
	 * 
	 * @param player
	 */
	public static void updateRegistries(EntityPlayer player) {
		List<KnownPack> known_packs = ServerEntry.server.getResourceManager().listPacks().flatMap((resourcepack) -> {
			return resourcepack.location().knownPackInfo().stream();
		}).toList();
		ClientConnection.sendPacket(player, new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(ServerEntry.server.getWorldData().enabledFeatures())));
		DynamicOps<NBTBase> dynamic_ops = ServerEntry.registryAccess.createSerializationContext(DynamicOpsNBT.INSTANCE);
		RegistrySynchronization.packRegistries(dynamic_ops, ServerEntry.registries.getAccessFrom(RegistryLayer.WORLDGEN), Set.copyOf(known_packs), (resourcekey, list) -> {
			ClientConnection.sendPacket(player, new ClientboundRegistryDataPacket(resourcekey, list));
		});
		ClientConnection.sendPacket(player, new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(ServerEntry.registries)));
	}

	public static void updateRegistries() {
		List<KnownPack> known_packs = ServerEntry.server.getResourceManager().listPacks().flatMap((resourcepack) -> {
			return resourcepack.location().knownPackInfo().stream();
		}).toList();
		PlayerList players = ServerEntry.server.getPlayerList();
		for (EntityPlayer player : players.players) {
			ClientConnection.sendPacket(player, new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(ServerEntry.server.getWorldData().enabledFeatures())));
			DynamicOps<NBTBase> dynamic_ops = ServerEntry.registryAccess.createSerializationContext(DynamicOpsNBT.INSTANCE);
			RegistrySynchronization.packRegistries(dynamic_ops, ServerEntry.registries.getAccessFrom(RegistryLayer.WORLDGEN), Set.copyOf(known_packs), (resourcekey, list) -> {
				ClientConnection.sendPacket(player, new ClientboundRegistryDataPacket(resourcekey, list));
			});
			ClientConnection.sendPacket(player, new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(ServerEntry.registries)));
		}
	}

	/**
	 * 异步推送注册表更新
	 * 
	 * @param player
	 */
	public static void updateRegistriesAnsyc(EntityPlayer player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateRegistries(player);
			}
		}).start();
	}

	public static void updateRegistriesAnsyc() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateRegistries();
			}
		}).start();
	}

	/**
	 * 让客户端更新区块
	 * 
	 * @param player
	 * @param chunk
	 */
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

	public static void updateDimensionType(EntityPlayer player, Holder<DimensionManager> dim_type) {
		CommonPlayerSpawnInfo respawn_info = player.createCommonSpawnInfo(player.serverLevel());
		NMSManipulator.setObject(respawn_info, "net.minecraft.network.protocol.game.CommonPlayerSpawnInfo.dimensionType", dim_type);
		sendClientboundRespawnPacket(player, new PacketPlayOutRespawn(respawn_info, (byte) 3));
	}

	public static void updateDimensionType(Player player, LevelStem.Type dim_type) {
		updateDimensionType(((CraftPlayer) player).getHandle(), DimensionType.getDimensionTypeHolder(dim_type));
	}

	public static void updateDimensionType(Player player, String dim_type) {
		updateDimensionType(((CraftPlayer) player).getHandle(), DimensionType.getDimensionTypeHolder(dim_type));
	}

	/**
	 * 由于客户端处理ClientboundRespawnPacket时会检测玩家当前dimension与packet指定的dimension是否相同来决定是否刷新世界ClientLevel
	 * 如果要刷新世界，则要先发送一个不同于玩家原本世界的ClientboundRespawnPacket再发送原本世界的packet
	 * 
	 * @param packet
	 */
	public static void sendClientboundRespawnPacket(EntityPlayer player, PacketPlayOutRespawn packet) {
		ResourceKey<net.minecraft.world.level.World> player_dim = player.serverLevel().dimension();
		if (packet.commonPlayerSpawnInfo().dimension().equals(player_dim)) {
			CommonPlayerSpawnInfo refresh_info = player.createCommonSpawnInfo(player.serverLevel());
			Vec3D pos = player.position();
			@SuppressWarnings("unused")
			Vec3D eye_dir = player.getLookAngle();
			if (player_dim.equals(WorldDimension.OVERWORLD)) {
				NMSManipulator.setObject(refresh_info, "net.minecraft.network.protocol.game.CommonPlayerSpawnInfo.dimension", WorldDimension.END);
				// NMSManipulator.setObject(refresh_info,
				// "net.minecraft.network.protocol.game.CommonPlayerSpawnInfo.dimensionType",
				// DimensionType.getDimensionTypeHolder(LevelStem.Type.OVERWORLD));
			} else
				NMSManipulator.setObject(refresh_info, "net.minecraft.network.protocol.game.CommonPlayerSpawnInfo.dimension", WorldDimension.OVERWORLD);
			ClientConnection.sendPacket(player, new PacketPlayOutRespawn(refresh_info, (byte) 3));
			player.teleportTo(pos.x, pos.y, pos.z);
			// player.lookAt(ArgumentAnchor.Anchor.EYES, eye_dir);
		}
		ClientConnection.sendPacket(player, packet);
	}
}
