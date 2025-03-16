package lib.crescent.nms.core;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import lib.crescent.nms.ServerEntry;
import lib.lunar.nativemc.NMSManipulator;
import lib.lunar.nativemc.Version;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
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

	static {
		enchantment = getRegistry(Registries.ENCHANTMENT);
		item = getRegistry(Registries.ITEM);
		dimension = getRegistry(Registries.DIMENSION);
		dimension_type = getRegistry(Registries.DIMENSION_TYPE);
		level_stem = getRegistry(Registries.LEVEL_STEM);
		biome = getRegistry(Registries.BIOME);
	}

	/**
	 * 获取注册表
	 * 
	 * @param <T>          ResourceKey类型
	 * @param resource_key ResourceKey参数定义于net.minecraft.core.registries.Registries
	 * @return 返回注册表实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> IRegistry<T> getRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		IRegistry<T> registry = null;
		if (Version.this_version.between("1_21_R0", "1_21_R2"))
			registry = (IRegistry<T>) (((Optional<Registry<T>>) (NMSManipulator.invoke(ServerEntry.registryAccess, "net.minecraft.core.RegistryAccess.registry(net.minecraft.resources.ResourceKey)", new Class<?>[] { resource_key.getClass() }, resource_key))).orElseThrow());// 1.21.0为registry()
		else if (Version.this_version.newerOrEqual("1_21_R2"))
			registry = (IRegistry<T>) (((Optional<Registry<T>>) (NMSManipulator.invoke(ServerEntry.registryAccess, "net.minecraft.core.RegistryAccess.lookup(net.minecraft.resources.ResourceKey)", new Class<?>[] { resource_key.getClass() }, resource_key))).orElseThrow());// 1.21.3及以上为lookup()
		if (registry == null)
			Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Registry of " + resource_key);
		return registry;
	}

	/**
	 * 
	 * @param <T>
	 * @param reg          注册表条目
	 * @param resource_key 该条目下的注册表键值对的key
	 * @return 对应resource_key的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRegistryValue(IRegistry<T> reg, ResourceKey<T> resource_key) {
		T reg_value = null;
		if (Version.this_version.between("1_21_R0", "1_21_R2"))
			reg_value = (T) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.get(net.minecraft.resources.ResourceKey)", new Class<?>[] { resource_key.getClass() }, resource_key));// 1.21.0为get()
		else if (Version.this_version.newerOrEqual("1_21_R2"))
			reg_value = (T) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.getValue(net.minecraft.resources.ResourceKey)", new Class<?>[] { resource_key.getClass() }, resource_key));// 1.21.3及以上为getValue()
		if (reg_value == null)
			Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Registry value of " + resource_key);
		return reg_value;
	}

	/**
	 * 
	 * @param <T>
	 * @param reg          注册表条目
	 * @param resource_key 该条目下的注册表键值对的key
	 * @return 对应resource_key的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRegistryValue(IRegistry<T> reg, MinecraftKey resource_loc) {
		T reg_value = null;
		if (Version.this_version.between("1_21_R0", "1_21_R2"))
			reg_value = (T) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.get(net.minecraft.resources.ResourceLocation)", new Class<?>[] { resource_loc.getClass() }, resource_loc));// 1.21.0为get()
		else if (Version.this_version.newerOrEqual("1_21_R2"))
			reg_value = (T) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.getValue(net.minecraft.resources.ResourceLocation)", new Class<?>[] { resource_loc.getClass() }, resource_loc));// 1.21.3及以上为getValue()
		if (reg_value == null)
			Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Registry value of " + resource_loc);
		return reg_value;
	}

	public static <T> T getRegistryValue(IRegistry<T> reg, ResourceKey<? extends IRegistry<T>> registries_entry, String reg_key) {
		return getRegistryValue(reg, ResourceLocationBuilder.getResourceKey(registries_entry, reg_key));
	}

	@SuppressWarnings("unchecked")
	public static <T> Holder.c<T> getRegistryHolder(IRegistry<T> reg, MinecraftKey resource_loc) {
		Holder.c<T> holder = null;
		if (Version.this_version.between("1_21_R0", "1_21_R2"))
			holder = ((Optional<Holder.c<T>>) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.getHolder(net.minecraft.resources.ResourceLocation)", new Class<?>[] { resource_loc.getClass() }, resource_loc))).orElse(null);// 1.21.0为getHolder()
		else if (Version.this_version.newerOrEqual("1_21_R2"))
			holder = ((Optional<Holder.c<T>>) (NMSManipulator.invoke(reg, "net.minecraft.core.Registry.get(net.minecraft.resources.ResourceLocation)", new Class<?>[] { resource_loc.getClass() }, resource_loc))).orElse(null);// 1.21.3及以上为get()
		if (holder == null)
			Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Registry value of " + resource_loc);
		return holder;
	}

	public static <T> boolean isFrozen(ResourceKey<? extends IRegistry<T>> resource_key) {
		return (boolean) NMSManipulator.access(getRegistry(resource_key), "net.minecraft.core.MappedRegistry.frozen");
	}

	/**
	 * 解冻注册表冻结以注册，不记录入is_registrise_frozen，因此尽量不要使用
	 * 
	 * @return 操作是否成功
	 */
	public static <T> boolean unfreezeRegistry(IRegistry<T> registry) {
		return NMSManipulator.setBoolean(registry, "net.minecraft.core.MappedRegistry.frozen", false) && NMSManipulator.setObject(registry, "net.minecraft.core.MappedRegistry.unregisteredIntrusiveHolders", new IdentityHashMap<>());
	}

	/**
	 * 解冻注册表冻结以注册，并记录入is_registrise_frozen，应当总是使用该方法解冻注册表
	 * 
	 * @return 操作是否成功
	 */
	public static <T> boolean unfreezeRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		return unfreezeRegistry(getRegistry(resource_key));
	}

	/**
	 * 冻结注册表，并记录入is_registrise_frozen
	 * 
	 * @return
	 */
	public static <T> void freezeRegistry(IRegistry<T> registry) {
		if (Version.this_version.between("1_21_R0", "1_21_R2"))
			registry.freeze();
		else if (Version.this_version.newerOrEqual("1_21_R2")) {
			Object tag_set_obj = TagKeys.getAllTagsSetObject(registry);
			Map<TagKey<T>, HolderSet.Named<T>> tag_map = new HashMap<>(TagKeys.getTagSetMap(tag_set_obj));
			Map<TagKey<T>, HolderSet.Named<T>> frozenTags = TagKeys.getFrozenTags(registry);
			tag_map.forEach(frozenTags::putIfAbsent);// 如果tag_map存在某个键值对，但frozenTags中没有该键值对，则拷贝到frozenTags中
			TagKeys.unboundAllTags(registry);
			registry.freeze();
			frozenTags.forEach(tag_map::putIfAbsent);
			TagKeys.setTagSetMap(tag_set_obj, tag_map);
			NMSManipulator.setObject(registry, "net.minecraft.core.MappedRegistry.allTags", tag_set_obj);
		}
	}

	public static <T> void freezeRegistry(ResourceKey<? extends IRegistry<T>> resource_key) {
		freezeRegistry(getRegistry(resource_key));
	}
}
