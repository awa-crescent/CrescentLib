package lib.crescent.utils.locale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

//语言系统支持
public class Locale {
	protected static Map<String, Locale> locale_entries = new HashMap<>();

	protected String plugin_name;
	protected String locale_folder;
	protected String locale;
	protected YamlConfiguration yml_entries;

	protected Locale(String plugin_name, String locale_folder, String locale) {
		yml_entries = new YamlConfiguration();
		this.plugin_name = plugin_name;
		this.locale_folder = locale_folder;
		setLocale(locale);
	}

	/**
	 * 
	 * @return 获取语言文件的绝对路径
	 */
	public String getAbsoluteLocaleFilePath() {
		String path = Bukkit.getServer().getPluginManager().getPlugin(plugin_name).getDataFolder().getAbsolutePath();
		if (locale_folder == null || locale_folder == "")// 如果没有设置locale_folder_path或者设置为空，则直接在配置文件根目录下搜索配置文件
			path += File.separator + locale + ".yml";
		else
			path += File.separator + locale_folder + File.separator + locale + ".yml";
		return path;
	}

	/**
	 * 加载本地化文件，优先加载数据文件夹的plugin_name/locale_folder/locale.yml配置文件，如果没有则加载jar包内置本地化文件plugin_jar/locale_folder/locale.yml
	 * 
	 * @return 返回是否加载成功
	 */
	public boolean loadLocaleFile() {
		boolean load_complete = true;
		File locale_file = new File(getAbsoluteLocaleFilePath());
		if (locale_file.exists()) {
			try {
				yml_entries.load(locale_file);
			} catch (FileNotFoundException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System cannot find locale file " + locale_file, ex);
				load_complete = false;
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System throws IOException when loading locale file " + locale_file, ex);
				load_complete = false;
			} catch (InvalidConfigurationException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System found invalid configuration in locale file " + locale_file, ex);
				load_complete = false;
			}
		} else {
			String locale_resource_path = (locale_folder == null || locale_folder == "") ? (locale + ".yml") : (locale_folder + '/' + locale + ".yml");
			InputStream locale_stream = Bukkit.getServer().getPluginManager().getPlugin(plugin_name).getResource(locale_resource_path);
			if (locale_stream == null) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System cannot find embeded locale file " + locale_resource_path + " in jar of plugin " + plugin_name);
				load_complete = false;
			}
			InputStreamReader locale_stream_reader = new InputStreamReader(locale_stream);
			try {
				yml_entries.load(locale_stream_reader);
				locale_stream_reader.close();
				locale_stream.close();
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System throws IOException when reading embeded locale file " + locale_resource_path + ".yml in jar of plugin " + plugin_name, ex);
				load_complete = false;
			} catch (InvalidConfigurationException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Locale System found invalid configuration in embeded locale file " + locale_resource_path, ex);
				load_complete = false;
			}
		}
		return load_complete;
	}

	/**
	 * 加载语言配置文件进本地化系统
	 * 
	 * @param plugin_name   插件名称
	 * @param locale_folder 相对getDataFolder()的路径
	 * @param locale        语言文件名称，不包含.yml
	 * @return 实例化的Locale对象
	 */
	public static Locale loadLocale(String plugin_name, String locale_folder, String locale) {
		Locale l = new Locale(plugin_name, locale_folder, locale);
		locale_entries.put(plugin_name, l);
		return l;
	}

	public boolean reload(String plugin_name) {
		Locale l = locale_entries.get(plugin_name);
		return locale_entries.get(plugin_name).setLocale(l.getLocale());
	}

	/**
	 * 设置本地化并立即加载语言文件，设置为相同的locale即可重载语言文件
	 * 
	 * @return 返回是否加载语言文件成功
	 */
	public boolean setLocale(String locale) {
		this.locale = locale;
		return loadLocaleFile();
	}

	/**
	 * 设置指定插件本地化并立即加载语言文件
	 * 
	 * @return 返回是否加载语言文件成功
	 */
	public static boolean setLocale(String plugin_name, String locale) {
		if (!locale_entries.containsKey(plugin_name))
			return false;
		return locale_entries.get(plugin_name).setLocale(locale);
	}

	public String getLocale() {
		return locale;
	}

	public static String getLocale(String plugin_name) {
		if (!locale_entries.containsKey(plugin_name))
			return null;
		return locale_entries.get(plugin_name).getLocale();
	}

	/**
	 * 根据locale得到本地化文本，不存在则返回key值
	 * 
	 * @param key 文本对应的key值
	 * @return 本地化文本
	 */
	public String getLocalizedValue(String key) {
		String value = yml_entries.getString(key);
		return value == null ? key : value;
	}

	/**
	 * 指定插件名称，根据locale得到本地化文本
	 * 
	 * @param plugin_name 插件名称
	 * @param key         文本对应的key值
	 * @return 本地化文本
	 */
	public static String getLocalizedValue(String plugin_name, String key) {
		return locale_entries.get(plugin_name).getLocalizedValue(key);
	}
}
