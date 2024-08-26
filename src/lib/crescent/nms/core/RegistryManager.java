package lib.crescent.nms.core;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;

import lib.crescent.Reflect;
import lib.crescent.nms.MappingsEntry;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class RegistryManager {
	public static final MinecraftServer server;

	public static final IRegistry<Enchantment> enchantment_registry;
	public static final IRegistry<Item> item_registry;
	public static final IRegistry<World> dimension_registry;
	public static final IRegistry<DimensionManager> dimension_manager_registry;

	private static HashMap<ResourceKey<? extends IRegistry<?>>, Boolean> registry_frozen_entries = new HashMap<>();

	static {
		System.out.println(MappingsEntry.getObfuscatedName("net.minecraft.core.MappedRegistry.frozen"));
		server = ((CraftServer) Bukkit.getServer()).getServer();
		enchantment_registry = getRegistry(Registries.ENCHANTMENT);
		registry_frozen_entries.put(Registries.ENCHANTMENT, true);
		item_registry = getRegistry(Registries.ITEM);
		registry_frozen_entries.put(Registries.ITEM, true);
		dimension_registry = getRegistry(Registries.DIMENSION);
		registry_frozen_entries.put(Registries.DIMENSION, true);
		dimension_manager_registry = getRegistry(Registries.DIMENSION_TYPE);
		registry_frozen_entries.put(Registries.DIMENSION_TYPE, true);
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
			registry = server.registryAccess().registry(resource_key).orElseThrow();
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
		return Reflect.setValue(registry, MappingsEntry.getObfuscatedName("net.minecraft.core.MappedRegistry.frozen"), false) && Reflect.setValue(registry, MappingsEntry.getObfuscatedName("net.minecraft.core.MappedRegistry.unregisteredIntrusiveHolders"), new IdentityHashMap<>());
	}

	/**
	 * 解冻注册表冻结以注册，并记录入is_registrise_frozen，应当总是使用该方法解冻注册表
	 * 
	 * @return 操作是否成功
	 */
	public static <T> boolean unfreezeRegistry(ResourceKey<IRegistry<T>> resource_key) {
		boolean op_state = unfreezeRegistry(getRegistry(resource_key));
		registry_frozen_entries.put(resource_key, !op_state);
		return op_state;
	}

	/**
	 * 冻结注册表，并记录入is_registrise_frozen
	 * 
	 * @return 操作是否成功
	 */
	public static <T> void freezeRegistry(ResourceKey<IRegistry<T>> resource_key) {
		getRegistry(resource_key).freeze();
		registry_frozen_entries.put(resource_key, true);
	}
}
