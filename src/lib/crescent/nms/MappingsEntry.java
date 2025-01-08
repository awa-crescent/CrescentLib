package lib.crescent.nms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * 统一使用Mojang Mappings
 *
 */
public abstract class MappingsEntry {
	private static YamlConfiguration mappings_entries;
	private static final int compatible_version_tolerance = 3;

	static {
		mappings_entries = new YamlConfiguration();
		InputStream mappings_stream;
		ClassLoader mappings_class_loader = MappingsEntry.class.getClassLoader();
		Version mappings_ver = Version.this_version;
		URL mappings_url = null;
		int try_times = compatible_version_tolerance;
		while (try_times >= 0) {
			--try_times;
			mappings_url = mappings_class_loader.getResource("lib/crescent/nms/mappings/" + mappings_ver + ".yml");
			if (mappings_url == null) {
				Bukkit.getLogger().log(Level.WARNING, "Cannot find embeded mappings file for NMS version " + mappings_ver);
				mappings_ver = mappings_ver.previous();
				continue;
			} else
				break;
		}
		if (mappings_url == null)
			Bukkit.getLogger().log(Level.SEVERE, "Cannot find compatible mappings version for current server NMS version " + Version.this_version + " with tolerance " + compatible_version_tolerance);
		else
			Bukkit.getLogger().log(Level.INFO, "Using mappings version " + mappings_ver);
		try {
			mappings_stream = mappings_url.openStream();
			InputStreamReader mappings_stream_reader = new InputStreamReader(mappings_stream);
			mappings_entries.load(mappings_stream_reader);
			mappings_stream_reader.close();
			mappings_stream.close();
		} catch (IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Parsing mappings file failed", ex);
		}
	}

	public static String getObfuscatedName(String mojang_name) {
		return mappings_entries.getString(mojang_name);
	}

	public static String getClassObfuscatedName(String nms_class_name) {
		return getObfuscatedName(nms_class_name + ".class");
	}
}
