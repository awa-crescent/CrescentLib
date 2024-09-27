package lib.crescent.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;

public class EntityUtils {
	public static boolean isHumanlikeEntity(Entity e) {
		return e instanceof HumanEntity || e instanceof Zombie || e instanceof AbstractSkeleton || e instanceof AbstractVillager || e instanceof Raider || e instanceof NPC;
	}

	public static void spawnParticle(Entity target, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra, Object data, boolean force) {
		target.getWorld().spawnParticle(particle, target.getLocation().add(0, target.getHeight() / 2, 0), count, offsetX, offsetY, offsetZ, extra, data, force);
	}

	public static void spawnParticle(Entity target, Particle particle, int count, double offsetX, double offsetY, double offsetZ, Object data) {
		target.getWorld().spawnParticle(particle, target.getLocation().add(0, target.getHeight() / 2, 0), count, offsetX, offsetY, offsetZ, 0, data, true);
	}

	public static void spawnParticle(Entity target, Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
		target.getWorld().spawnParticle(particle, target.getLocation().add(0, target.getHeight() / 2, 0), count, offsetX, offsetY, offsetZ, 0, null, true);
	}

	public static void cureEntity(LivingEntity entity, double cure_health) {
		double max_health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double new_health = entity.getHealth() + cure_health;
		if (new_health < max_health)
			entity.setHealth(new_health);
		else
			entity.setHealth(max_health);
	}

	public static void damageEntity(LivingEntity entity, double damage_health) {
		double new_health = entity.getHealth() - damage_health;
		if (new_health < 0)
			entity.setHealth(0);
		else
			entity.setHealth(new_health);
		entity.playHurtAnimation(0);
	}

	public static LivingEntity getDamageSourceEntity(Entity entity) {
		LivingEntity target = null;
		if (entity instanceof LivingEntity damager)
			target = damager;
		else if (entity instanceof Projectile projectile)
			target = (LivingEntity) projectile.getShooter();
		return target;
	}

	public static LivingEntity removeAllPotionEffects(LivingEntity entity) {
		if (entity == null)
			return null;
		Collection<PotionEffect> effects = entity.getActivePotionEffects();
		for (PotionEffect effect : effects)
			entity.removePotionEffect(effect.getType());
		return entity;
	}

	public static ArrayList<? extends Player> getAllPlayers(World world) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		ArrayList<Player> players_in_world = new ArrayList<>();
		for (Player player : players)
			if (player.getWorld() == world)
				players_in_world.add(player);
		return players_in_world;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> ArrayList<T> getNearbySpecifiedEntities(Entity entity, double x_offset, double y_offset, double z_offset, Class<T> clazz) {
		List<Entity> nearby_entities = entity.getNearbyEntities(x_offset, y_offset, z_offset);
		ArrayList<T> specified_entities = new ArrayList<>();
		for (Entity nearby_entity : nearby_entities)
			if (nearby_entity.getClass() == clazz) {
				specified_entities.add((T) nearby_entity);
			}
		return specified_entities;
	}

	/**
	 * 获取指定世界的所有玩家，并储存在players_list中，如果该列表有元素，则清空元素
	 * 
	 * @param world        要获取玩家的世界
	 * @param players_list 存放玩家对象的列表
	 * @return players_list
	 */
	public static ArrayList<Player> getAllPlayers(World world, ArrayList<Player> players_list) {
		players_list.clear();
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players)
			if (player.getWorld() == world)
				players_list.add(player);
		return players_list;
	}

	public static void playSoundToAllPlayers(Sound sound, float volume) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players)
			player.playSound(player.getLocation(), sound, volume, 0);
	}

	public static void playSoundToAllPlayers(World world, Sound sound, float volume) {
		Collection<? extends Player> players = getAllPlayers(world);
		for (Player player : players)
			player.playSound(player.getLocation(), sound, volume, 0);
	}

	public static void playSoundToNearbyPlayers(Entity entity, Sound sound, double x_offset, double y_offset, double z_offset, float volume) {
		List<Player> nearby_players = getNearbySpecifiedEntities(entity, x_offset, y_offset, z_offset, Player.class);
		for (Player nearby_player : nearby_players)
			nearby_player.playSound(nearby_player.getLocation(), sound, volume, 0);
	}

	/**
	 * 判断生物是否是亡灵生物
	 * 
	 * @param entity 要判断的实体
	 * @return 该实体是否是亡灵生物
	 */
	public static boolean isEntityUndead(LivingEntity entity) {
		return entity instanceof Zombie || entity instanceof AbstractSkeleton || entity instanceof Wither || entity instanceof Zoglin;
	}
}
