package lib.crescent.nms.core;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import lib.crescent.nms.NMSManipulator;
import lib.crescent.nms.ServerEntry;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;

public class RegistryManager {

	public static final IRegistry<Enchantment> enchantment;
	public static final IRegistry<Item> item;
	public static final IRegistry<World> dimension;
	public static final IRegistry<WorldDimension> level_stem;
	public static final IRegistry<DimensionManager> dimension_type;
	public static final IRegistry<BiomeBase> biome;

	private static HashMap<ResourceKey<? extends IRegistry<?>>, Boolean> registry_frozen_entries = new HashMap<>();

	static {
		enchantment = loadRegistry(Registries.ENCHANTMENT);
		item = loadRegistry(Registries.ITEM);
		dimension = loadRegistry(Registries.DIMENSION);
		dimension_type = loadRegistry(Registries.DIMENSION_TYPE);
		level_stem = loadRegistry(Registries.LEVEL_STEM);
		biome = loadRegistry(Registries.BIOME);
	}

	private static <T> IRegistry<T> loadRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		registry_frozen_entries.put(resource_key, true);
		return getRegistry(resource_key);
	}

	/**
	 * 
	 * @param <T>          ResourceKey类型
	 * @param resource_key ResourceKey参数定义于net.minecraft.core.registries.Registries
	 * @return 返回注册表实例
	 */
	public static <T> IRegistry<T> getRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		IRegistry<T> registry = null;
		try {
			registry = ServerEntry.server.registryAccess().registry(resource_key).orElseThrow();
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Registry of " + resource_key, ex);
		}
		return registry;
	}

	public static <T> boolean isFrozen(ResourceKey<? extends IRegistry<T>> resource_key) {
		return registry_frozen_entries.get(resource_key);
	}

	/**
	 * 解冻注册表冻结以注册，不记录入is_registrise_frozen，因此尽量不要使用
	 * 
	 * @return 操作是否成功
	 */
	public static <T> boolean unfreezeRegistry(IRegistry<T> registry) {
		return NMSManipulator.setBooleanValue(registry, "net.minecraft.core.MappedRegistry.frozen", false) && NMSManipulator.setObjectValue(registry, "net.minecraft.core.MappedRegistry.unregisteredIntrusiveHolders", new IdentityHashMap<>());
	}

	/**
	 * 解冻注册表冻结以注册，并记录入is_registrise_frozen，应当总是使用该方法解冻注册表
	 * 
	 * @return 操作是否成功
	 */
	public static <T> boolean unfreezeRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		boolean op_state = unfreezeRegistry(getRegistry(resource_key));
		registry_frozen_entries.put(resource_key, !op_state);
		return op_state;
	}

	/**
	 * 冻结注册表，并记录入is_registrise_frozen
	 * 
	 * @return 操作是否成功
	 */
	public static <T> void freezeRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		getRegistry(resource_key).freeze();
		registry_frozen_entries.put(resource_key, true);
	}
}
