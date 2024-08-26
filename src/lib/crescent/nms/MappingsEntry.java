package lib.crescent.nms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	static {
		mappings_entries = new YamlConfiguration();
		InputStream mappings_stream;
		try {
			mappings_stream = MappingsEntry.class.getClassLoader().getResource("lib/crescent/nms/Mappings.yml").openStream();
			if (mappings_stream == null) {
				Bukkit.getLogger().log(Level.SEVERE, "Cannot find embeded mappings file");
			}
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
}
