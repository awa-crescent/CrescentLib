package lib.crescent;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrescentLib extends JavaPlugin {
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 0;

	@Override
	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, VMEntry.NATIVE_JVM_BIT_VERSION + "-bit " + (VMEntry.NATIVE_JVM_HOTSPOT ? "HotSpot JVM" : "JVM") + " -UseCompressedOops = " + VMEntry.NATIVE_JVM_COMPRESSED_OOPS);

	}

	@Override
	public void onDisable() {

	}
}
