package lib.crescent.utils;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CooldownSet<K> {
	private Plugin plugin;
	private HashSet<K> cooldown_set = new HashSet<>();

	public CooldownSet(String plugin_name) {
		plugin = Bukkit.getPluginManager().getPlugin(plugin_name);
	}

	public <V extends Number> void startCooldown(K key, V cooldown_ticks) {
		cooldown_set.add(key);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				cooldown_set.remove(key);
			}
		}, cooldown_ticks.longValue());
	}

	public boolean isCooldownFinished(K key) {
		return !cooldown_set.contains(key);
	}

	public void reset(K key) {
		cooldown_set.remove(key);
	}
}
