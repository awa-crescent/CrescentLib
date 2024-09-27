package lib.crescent.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import lib.crescent.entity.EntityUtils;
import lib.crescent.utils.format.FormattingStyle;
import lib.crescent.utils.locale.Locale;

public class BossBarEffect implements Listener {
	private boolean event_registerd = false;
	private BossBar boss_bar;
	private String plugin_name;
	private LivingEntity attached_progress_entity = null;
	private LivingEntity attached_display_entity = null;
	private double display_range_offset_x, display_range_offset_y, display_range_offset_z;
	ArrayList<Player> nearby_players = new ArrayList<>();
	private BukkitTask run_update_progress = null;
	private BukkitTask run_update_display = null;

	public BossBarEffect(BarColor color, BarStyle style, String plugin_name, String display_name_key, FormattingStyle display_style) {
		this.plugin_name = plugin_name;
		boss_bar = Bukkit.createBossBar(plugin_name == null || display_name_key == null ? "" : (display_style == null ? Locale.getLocalizedValue(plugin_name, display_name_key) : display_style.formatStringJSON(Locale.getLocalizedValue(plugin_name, display_name_key))), color == null ? BarColor.WHITE : color, style == null ? BarStyle.SOLID : style);
	}

	public BossBarEffect(String plugin_name, String display_name_key, FormattingStyle style) {
		this(null, null, plugin_name, display_name_key, style);
	}

	public BossBarEffect(String plugin_name, String display_name_key) {
		this(null, null, plugin_name, display_name_key, null);
	}

	public BossBarEffect() {
		this(null, null, null, null, null);
	}

	protected final BukkitTask runUpdateFunc(Runnable runnable, long update_tick) {
		return Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin(plugin_name), runnable, 0, update_tick);
	}

	/**
	 * 添加关联进度的实体，该实体的血量与进度有关，进度就是血量百分比
	 * 
	 * @param entity 关联血量和进度的实体
	 * @return
	 */
	public final BossBarEffect attachProgressEntity(LivingEntity entity) {
		attached_progress_entity = entity;
		AttributeInstance max_health = attached_progress_entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		boss_bar.setProgress(attached_progress_entity.getHealth() / max_health.getBaseValue());
		run_update_progress = runUpdateFunc(new Runnable() {
			@Override
			public void run() {
				if (attached_progress_entity != null)
					boss_bar.setProgress(attached_progress_entity.getHealth() / max_health.getBaseValue());
			}
		}, 10);
		if (!event_registerd)
			Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getServer().getPluginManager().getPlugin(plugin_name));
		event_registerd = true;
		return this;
	}

	/**
	 * 取消关联进度的实体
	 * 
	 * @return
	 */
	public final BossBarEffect detachProgressEntity() {
		attached_progress_entity = null;
		if (run_update_progress != null) {
			run_update_progress.cancel();
			run_update_progress = null;
		}
		return this;
	}

	/**
	 * 指定显示BossBar的实体，进入范围内的玩家将自动显示效果，离开的、没有进入范围内的玩家不显示效果。实体周围跟别的办法添加的会显示BossBar的玩家是分开控制的。例如可以给范围外的一个玩家单独添加BossBar。但是如果单独添加BossBar的玩家进入范围，则会将该玩家归entity范围管理，一旦离开范围就会取消BossBar
	 * 
	 * @param entity      实体
	 * @param range       显示距离，只要有一个负数就表示整个世界
	 * @param add_to_self 是否实体自己也能看见BossBar，仅对玩家有效
	 * @return
	 */
	public final BossBarEffect attachDisplayEntity(LivingEntity entity, double range_x, double range_y, double range_z, boolean add_to_self) {
		attached_display_entity = entity;
		this.display_range_offset_x = range_x;
		this.display_range_offset_y = range_y;
		this.display_range_offset_z = range_z;
		run_update_display = runUpdateFunc(new Runnable() {
			@Override
			public void run() {
				if (attached_display_entity != null) {
					removeNearbyPlayers();
					if (display_range_offset_x < 0 || display_range_offset_y < 0 || display_range_offset_z < 0) {
						EntityUtils.getAllPlayers(attached_display_entity.getWorld(), nearby_players);
						addToPlayers(nearby_players);
					} else
						addToNearbyPlayers();
				}
			}
		}, 20);
		if (add_to_self && entity instanceof Player player)
			boss_bar.addPlayer(player);
		return this;
	}

	protected BossBarEffect removeNearbyPlayers() {
		for (Player nearby_player : nearby_players)
			boss_bar.removePlayer(nearby_player);
		nearby_players.clear();
		return this;
	}

	protected BossBarEffect addToNearbyPlayers() {
		List<Entity> nearby_entities = attached_display_entity.getNearbyEntities(display_range_offset_x, display_range_offset_y, display_range_offset_z);
		for (Entity nearby_entity : nearby_entities)
			if (nearby_entity instanceof Player player) {
				boss_bar.addPlayer(player);
				nearby_players.add(player);
			}
		return this;
	}

	/**
	 * 取消关联显示BossBar的实体
	 * 
	 * @return
	 */
	public final BossBarEffect detachDisplayEntity() {
		if (run_update_display != null) {
			run_update_display.cancel();
			run_update_display = null;
		}
		removeNearbyPlayers();
		if (attached_display_entity instanceof Player player)
			boss_bar.removePlayer(player);
		attached_display_entity = null;
		return this;
	}

	public BossBarEffect setVisible(boolean visible) {
		boss_bar.setVisible(visible);
		return this;
	}

	public boolean isVisible() {
		return boss_bar.isVisible();
	}

	/**
	 * 单独一次性添加一个玩家显示BossBar，不计入追踪。但是如果单独添加BossBar的玩家进入范围，则会将该玩家归entity范围管理，一旦离开范围就会取消BossBar
	 * 
	 * @param player 玩家
	 * @return
	 */
	public BossBarEffect addToPlayer(Player player) {
		boss_bar.addPlayer(player);
		return this;
	}

	/**
	 * 单独一次性添加一群玩家显示BossBar，不计入追踪。但是如果单独添加BossBar的玩家进入范围，则会将这些玩家归entity范围管理，一旦离开范围就会取消BossBar
	 * 
	 * @param players 玩家列表
	 * @return
	 */
	public BossBarEffect addToPlayers(ArrayList<? extends Player> players) {
		for (Player player : players)
			boss_bar.addPlayer(player);
		return this;
	}

	/**
	 * 单独一次性添加一个世界的所有玩家显示BossBar，不计入追踪。但是如果单独添加BossBar的玩家进入范围，则会将这些玩家归entity范围管理，一旦离开范围就会取消BossBar
	 * 注意：如果attachDisplayEntity()添加的实体显示范围是整个世界的话，则该方法无效，这个世界的BossBar均归那个实体管理。因此，该方法一般仅限没有attachDisplayEntity()添加实体的时候使用
	 * 
	 * @param wolrd 世界
	 * @return
	 */
	public BossBarEffect addToAllPlayersInWorld(World wolrd) {
		ArrayList<? extends Player> players = EntityUtils.getAllPlayers(wolrd);
		return addToPlayers(players);
	}

	/**
	 * 单独一次性添加某个实体附近玩家显示BossBar，不计入追踪。但是如果单独添加BossBar的玩家进入范围，则会将这些玩家归entity范围管理，一旦离开范围就会取消BossBar
	 * 
	 * @param entity   以该实体位置为中心
	 * @param x_offset 1/2x轴长度
	 * @param y_offset 1/2y轴长度
	 * @param z_offset 1/2z轴长度
	 * @return
	 */
	public BossBarEffect addToAllPlayersNearbyEntity(Entity entity, double x_offset, double y_offset, double z_offset) {
		List<Entity> nearby_entities = entity.getNearbyEntities(x_offset, y_offset, z_offset);
		for (Entity nearby_entity : nearby_entities)
			if (nearby_entity instanceof Player player)
				boss_bar.addPlayer(player);
		return this;
	}

	/**
	 * 单独一次性移除一个玩家显示BossBar，如果玩家在attachDisplayEntity()实体范围内则会又显示BossBar
	 * 
	 * @param player 玩家
	 * @return
	 */
	public BossBarEffect removePlayer(Player player) {
		boss_bar.removePlayer(player);
		return this;
	}

	/**
	 * 单独一次性移除整个世界所有玩家显示BossBar，如果玩家在attachDisplayEntity()实体范围内则会又显示BossBar
	 * 
	 * @param wolrd
	 * @return
	 */
	public BossBarEffect removeAllPlayersInWorld(World wolrd) {
		ArrayList<? extends Player> players = EntityUtils.getAllPlayers(wolrd);
		for (Player player : players)
			boss_bar.removePlayer(player);
		return this;
	}

	/**
	 * 
	 * 单独一次性移除某个实体附近玩家显示BossBar，如果玩家在attachDisplayEntity()实体范围内则会又显示BossBar
	 * 
	 * @param entity   以该实体位置为中心
	 * @param x_offset 1/2x轴长度
	 * @param y_offset 1/2y轴长度
	 * @param z_offset 1/2z轴长度
	 * @return
	 */
	public BossBarEffect removeAllPlayersNearbyEntity(Entity entity, double x_offset, double y_offset, double z_offset) {
		List<Entity> nearby_entities = entity.getNearbyEntities(x_offset, y_offset, z_offset);
		for (Entity nearby_entity : nearby_entities)
			if (nearby_entity instanceof Player player)
				boss_bar.removePlayer(player);
		return this;
	}

	/**
	 * 一次性移除所有玩家的BossBar，如果玩家在attachDisplayEntity()实体范围内则会又显示BossBar
	 * 
	 * @return
	 */
	public BossBarEffect removeAllPlayers() {
		boss_bar.removeAll();
		return this;
	}

	/**
	 * 单独一次性添加attached_display_entity附近玩家显示BossBar，不计入追踪。但是如果单独添加BossBar的玩家进入范围，则会将这些玩家归attached_display_entity范围管理，一旦离开范围就会取消BossBar
	 * 
	 * @param entity   以该实体位置为中心
	 * @param x_offset 1/2x轴长度
	 * @param y_offset 1/2y轴长度
	 * @param z_offset 1/2z轴长度
	 * @return
	 */
	public BossBarEffect addToNearbyPlayers(double x_offset, double y_offset, double z_offset) {
		return addToAllPlayersNearbyEntity(attached_display_entity, x_offset, y_offset, z_offset);
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (event.getEntity() == attached_progress_entity) {
			detachProgressEntity();
		}
		if (event.getEntity() == attached_display_entity) {
			boss_bar.setVisible(false);
			boss_bar.removeAll();
			detachDisplayEntity();
		}
	}

	public BossBarEffect addFlag(BarFlag flag) {
		boss_bar.addFlag(flag);
		return this;
	}

	public BossBarEffect removeFlag(BarFlag flag) {
		boss_bar.removeFlag(flag);
		return this;
	}

	public static BossBarEffect createBossBarVisionEffect(String plugin_name, String display_name_key, FormattingStyle style, boolean darken_sky, boolean create_fog, boolean play_dragon_music) {
		BossBarEffect vision_effect = new BossBarEffect(plugin_name, display_name_key).setVisible(true);
		if (darken_sky)
			vision_effect.addFlag(BarFlag.DARKEN_SKY);
		if (create_fog)
			vision_effect.addFlag(BarFlag.CREATE_FOG);
		if (play_dragon_music)
			vision_effect.addFlag(BarFlag.PLAY_BOSS_MUSIC);
		return vision_effect;
	}

	public static BossBarEffect createBossBarVisionEffect(String plugin_name, String display_name_key, FormattingStyle style, boolean darken_sky, boolean create_fog) {
		return createBossBarVisionEffect(plugin_name, display_name_key, style, darken_sky, create_fog, false);
	}

}
