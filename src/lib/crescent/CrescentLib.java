package lib.crescent;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrescentLib extends JavaPlugin {
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 0;

	@Override
	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, "CrescentLib loaded.");
	}

	@Override
	public void onDisable() {

	}
}
