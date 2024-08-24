package lib.crescent.utils.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class AutoSerialization implements Listener {
	public static final long DEFAULT_AUTOSERIALIZATION_TIME = 100;
	private String serializable_map_filepath;
	private HashMap<String, Serializable> serializable_map;
	private BukkitTask auto_serialize;

	String buf_filepath_str;
	Path original_filepath;
	Path tmp_filepath;
	Path buf_filepath;

	private static HashMap<String, AutoSerialization> auto_serialization_pluginset = new HashMap<>();

	/**
	 * 构建自动序列化，传入序列化文件存放目录，将自动开始反序列化已有的文件，并隔段时间就序列化写入文件一次
	 * 
	 * @param plugin_name                 插件名称
	 * @param serializable_datafolder     序列化对象的存放目录，始终在Plugin的getDataFolder()目录下
	 * @param auto_serialize_time_in_tick 间隔多长时间自动序列化并写入文件，单位tick
	 */
	@SuppressWarnings("unchecked")
	public AutoSerialization(String plugin_name, String serializable_datafolder, String serializable_map_filename, long auto_serialize_time_in_tick) {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(plugin_name);
		if (serializable_map_filename == "" || serializable_map_filename == null)
			serializable_map_filename = "serializable_map";
		String serializable_map_filefolder = plugin.getDataFolder().getAbsolutePath() + ((serializable_datafolder == null || serializable_datafolder == "") ? File.separator : File.separator + serializable_datafolder + File.separator);
		new File(serializable_map_filefolder).mkdirs();
		serializable_map_filepath = serializable_map_filefolder + serializable_map_filename;
		buf_filepath_str = serializable_map_filepath + "_buf";
		// 由于FileOutputStream不会自动创建多级目录，因此先手动创建
		original_filepath = Path.of(serializable_map_filepath);
		tmp_filepath = Path.of(serializable_map_filepath + "_tmp");
		buf_filepath = Path.of(buf_filepath_str);
		File original = original_filepath.toFile();
		try {
			original.createNewFile();
		} catch (IOException ex) {
		}
		serializable_map = (HashMap<String, Serializable>) deserialize(serializable_map_filepath);// 存在序列化后的serializable_map则加载它，否则新建
		if (serializable_map == null)
			serializable_map = new HashMap<>();
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		auto_serialize = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				serializeAllObjects();
			}
		}, auto_serialize_time_in_tick, auto_serialize_time_in_tick);
	}

	public static void boot(String plugin_name, String serializable_datafolder, String serializable_map_filename, long auto_serialize_time_in_tick) {
		auto_serialization_pluginset.put(plugin_name, new AutoSerialization(plugin_name, serializable_datafolder, serializable_map_filename, auto_serialize_time_in_tick));
	}

	public static void boot(String plugin_name, String serializable_datafolder, String serializable_map_filename) {
		boot(plugin_name, serializable_datafolder, serializable_map_filename, DEFAULT_AUTOSERIALIZATION_TIME);
	}

	public static void boot(String plugin_name, String serializable_datafolder) {
		boot(plugin_name, serializable_datafolder, null);
	}

	public static void boot(String plugin_name) {
		boot(plugin_name, "serializable_objects");
	}

	@EventHandler
	public void onPluginDisableEvent(PluginDisableEvent event) {
		auto_serialize.cancel();
		serializeAllObjects();
	}

	/**
	 * 注册序列化对象，被注册以后才可以托管给自动序列化。如果一个类需要将其成员托管给自动序列化，则需要在其构造函数注册要托管的对象
	 * 
	 * @param ref_name 该对象的引用名称，要通过该名称才可以在反序列化后查找回来，名称与序列化后的文件名一致
	 * @param obj      要托管的对象
	 */
	public AutoSerialization pushObject(String ref_name, Serializable obj) {
		if (obj instanceof AutoSerializable as)
			as.onPush();
		serializable_map.put(ref_name, obj);
		return this;
	}

	/**
	 * 获取给定引用名称对应的对象，如果不存在则返回null
	 * 
	 * @param ref_name 对象的引用名称
	 * @return
	 */
	public Serializable pullObject(String ref_name) {
		Serializable obj = serializable_map.get(ref_name);
		if (obj instanceof AutoSerializable as)
			as.onPull();
		return obj;
	}

	/**
	 * 获取给定引用名称对应的对象，如果不存在则将给定的obj存入ref_name并返回该对象
	 * 
	 * @param ref_name 对象的引用名称
	 * @param obj      如果目标对象不存在则新加的对象
	 * @return 获取到的对象引用
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T pullObject(String ref_name, T obj) {
		T deserialized_obj = (T) pullObject(ref_name);
		if (deserialized_obj == null) {// 如果该引用名称对应的对象不在序列化列表里，则将obj存入该引用名称
			pushObject(ref_name, obj);
			deserialized_obj = obj;
		}
		return deserialized_obj;
	}

	public static AutoSerialization pushObject(String plugin_name, String ref_name, Serializable obj) {
		return auto_serialization_pluginset.get(plugin_name).pushObject(ref_name, obj);
	}

	public static Serializable pullObject(String plugin_name, String ref_name) {
		return auto_serialization_pluginset.get(plugin_name).pullObject(ref_name);
	}

	public static <T extends Serializable> T pullObject(String plugin_name, String ref_name, T obj) {
		return auto_serialization_pluginset.get(plugin_name).pullObject(ref_name, obj);
	}

	/**
	 * 执行自动序列化操作，本次序列化后的对象将存入另一个缓冲中，直到写入完成才将会将该缓冲的文件名称改为实际的serializable_map文件（即缓冲交换），防止序列化过程中服务器崩溃导致序列化对象丢失。
	 * 
	 * @return
	 */
	protected AutoSerialization serializeAllObjects() {
		serialize(serializable_map, buf_filepath_str);
		try {
			// 双缓冲
			Files.move(original_filepath, tmp_filepath, StandardCopyOption.REPLACE_EXISTING);
			Files.move(buf_filepath, original_filepath, StandardCopyOption.REPLACE_EXISTING);
			Files.move(tmp_filepath, buf_filepath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "AutoSerialization cannot swap buffer file for " + serializable_map_filepath, ex);
		}
		return this;
	}

	/**
	 * 反序列化一个文件
	 * 
	 * @param file_path 序列化对象的文件地址
	 * @return 反序列化的对象
	 */
	public static Serializable deserialize(String file_path) {
		Serializable obj = null;
		try {
			FileInputStream serializable_file = new FileInputStream(file_path);
			ObjectInputStream serializable_s = new ObjectInputStream(serializable_file);
			obj = (Serializable) serializable_s.readObject();
			serializable_s.close();
			serializable_file.close();
		} catch (IOException | ClassNotFoundException ex) {
			obj = null;
		}
		return obj;
	}

	/**
	 * 序列化一个对象
	 * 
	 * @param obj       要序列化的对象
	 * @param file_path 序列化后储存的地址
	 */
	public static void serialize(Serializable obj, String file_path) {
		try {
			FileOutputStream serializable_files = new FileOutputStream(file_path);
			ObjectOutputStream serializable_s = new ObjectOutputStream(serializable_files);
			serializable_s.writeObject(obj);
			serializable_s.close();
			serializable_files.close();
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Serialize " + file_path + " failed", ex);
		}
	}

}
