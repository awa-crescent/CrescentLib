package lib.crescent.nms.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import lib.crescent.Reflect;
import lib.crescent.nms.Mappings;
import lib.crescent.tag.NamespacedKeyUtils;
import lib.crescent.tag.Tag;

public class HolderSetUtils {

	@SuppressWarnings("unchecked")
	public static <T> List<Holder<T>> get_HolderSet_contents(HolderSet<T> holder_set) {
		return (List<Holder<T>>) Reflect.getValue(holder_set, Mappings.net.minecraft.core.HolderSet.Named.contents);
	}

	public static <T> boolean modify_HolderSet_contents(HolderSet<T> holder_set, List<Holder<T>> contents) {
		boolean modify_result = false;
		// HolderSet分两种，Named和Direct。两者均是ListBacked的子类，而ListBacked是唯一实现了HolderSet<T>接口的类。Named为有TagKey命名的冲突集合，Direct是没有TagKey的冲突集合。原版附魔冲突都是Named，而本库则不给冲突集合命名TagKey，因此是Direct
		if (holder_set instanceof HolderSet.Named)
			modify_result = Reflect.setValue(holder_set, Mappings.net.minecraft.core.HolderSet.Named.contents, contents);
		else if (holder_set instanceof HolderSet.a) // HolderSet.Direct
			modify_result = Reflect.setValue(holder_set, Mappings.net.minecraft.core.HolderSet.Direct.contents, contents) && Reflect.setValue(holder_set, Mappings.net.minecraft.core.HolderSet.Direct.contentsSet, null);
		return modify_result;
	}

	/**
	 * 此key为TagKey，为该HolderSet.Named的名称
	 * 
	 * @param <T>
	 * @param holder_set
	 * @return
	 */
	public static <T> TagKey<T> getKey(HolderSet<T> holder_set) {
		return holder_set instanceof HolderSet.Named<T> named ? named.key() : null;
	}

	public static <T> String getKeyNamespacedID(HolderSet<T> holder_set) {
		return TagUtils.getTagKeyNamespacedID(getKey(holder_set));
	}

	@SuppressWarnings("unchecked")
	public static <T> HolderSet.a<T> createHolderSetDirect(ResourceKey<? extends IRegistry<T>> resource_key, Set<String> namespaced_members) {
		IRegistry<T> registry = RegistryManager.getRegistry(resource_key);// 获取resource_key的注册表
		List<Holder<T>> contents = new ArrayList<>();
		for (String memb : namespaced_members) {
			MinecraftKey nms_key = NamespacedKeyUtils.getResourceLocationFromNamespacedID(memb);
			Holder.c<T> memb_holder;// memb_holder类型为Holder$Reference，表示要添加的目标成员引用
			try {
				memb_holder = registry.getHolder(nms_key).orElseThrow();// 获取要添加的目标成员引用，目标不存在则抛出异常
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Holder of " + nms_key + ". It may doesn't exist");
				throw ex;
			}
			contents.add(memb_holder);
		}
		return (HolderSet.a<T>) Reflect.construct(HolderSet.a.class, List.class, contents);
	}

	public static HolderSet.a<Item> createDirectItemSet(Set<String> namespaced_members) {
		return createHolderSetDirect(Registries.ITEM, namespaced_members);
	}

	public static <T> String toString(Holder<T> holder) {
		return holder.getRegisteredName();
	}

	public static <T> ResourceLocation toResourceLocation(Holder<T> holder) {
		return new ResourceLocation(toString(holder));
	}

	public static <T> List<String> toString(HolderSet<T> holder_set) {
		List<String> list = new ArrayList<>();
		List<Holder<T>> contents = get_HolderSet_contents(holder_set);
		for (Holder<T> holder : contents)
			list.add(toString(holder));
		return list;
	}

	public static <T> List<ResourceLocation> toResourceLocation(HolderSet<T> holder_set) {
		List<ResourceLocation> list = new ArrayList<>();
		List<Holder<T>> contents = get_HolderSet_contents(holder_set);
		for (Holder<T> holder : contents)
			list.add(toResourceLocation(holder));
		return list;
	}

	public static <T> Tag toTag(HolderSet<T> holder_set) {
		Set<String> set = new HashSet<>();
		List<Holder<T>> contents = get_HolderSet_contents(holder_set);
		for (Holder<T> holder : contents)
			set.add(toString(holder));
		return new Tag(getKeyNamespacedID(holder_set), set);
	}
}
