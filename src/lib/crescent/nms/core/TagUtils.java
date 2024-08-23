package lib.crescent.nms.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import lib.crescent.Reflect;
import lib.crescent.nms.Mappings;
import lib.crescent.tag.NamespacedKeyUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagUtils {

	public static <T> TagKey<T> getTagKey(ResourceKey<? extends IRegistry<T>> resource_key, String resource_location) {
		return TagKey.create(resource_key, NamespacedKeyUtils.getResourceLocationFromNamespacedID(resource_location));
	}

	public static <T> String getTagKeyNamespacedID(TagKey<T> tag_key) {
		return tag_key == null ? null : tag_key.location().toString();
	}

	/**
	 * 添加Tag下的成员，如果该Tag不存在则新建一个Tag
	 * 
	 * @param <T>                ResourceKey所属的类型，为NMS类型
	 * @param resource_key       要创建HolderSet的ResourceKey，可通过Registries获取
	 * @param namespaced_tag     要添加成员的tag名称带命名空间，可以加"/"用以区分命名空间
	 * @param namespaced_members 带命名空间的成员，为目标的成员，例如物品、方块、附魔等
	 * @return 创建成功的Tag成员集合，即HolderSet
	 */
	@SuppressWarnings("unchecked")
	public static <T> HolderSet.Named<T> appendTagMembers(ResourceKey<? extends IRegistry<T>> resource_key, String namespaced_tag, Set<String> namespaced_members) {
		IRegistry<T> registry = RegistryManager.getRegistry(resource_key);// 获取resource_key的注册表
		TagKey<T> tag_key = getTagKey(resource_key, namespaced_tag); // 为新的tag创建一个TagKey
		HolderSet.Named<T> tag_members = ((IRegistry<T>) registry).getOrCreateTag(tag_key);// 判断注册表中是否存在该tag的成员列表，存在则获取，不存在则新建
		List<Holder<T>> contents = new ArrayList<>();// 新的成员列表，将替换目标注册表下对应TagKey的HolderSet$Named.contents
		for (String memb : namespaced_members) {
			MinecraftKey nms_key = NamespacedKeyUtils.getResourceLocationFromNamespacedID(memb);// 获取每个成员的ResourceLocation(Spigot API反混淆为MinecraftKey)
			Holder.c<T> memb_holder;// memb_holder类型为Holder$Reference，表示要添加的目标成员引用
			try {
				memb_holder = registry.getHolder(nms_key).orElseThrow();// 获取要添加的目标成员引用，目标不存在则抛出异常
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "NMS cannot get Holder of " + nms_key + ". It may doesn't exist");
				throw ex;
			}
			// 获取该目标成员引用的其他tag，并添加新tag后一起写入Holder$Reference.tags
			Set<TagKey<T>> memb_tags = new HashSet<TagKey<T>>((Set<TagKey<T>>) Reflect.getValue(memb_holder, Mappings.net.minecraft.core.Holder.Reference.tags));// 复制holder所有的tags，更改后将替换原有的Holder$Reference.tags
			memb_tags.add((TagKey<T>) tag_key);// 添加新的自定义的tag_key，如果已经存在则忽略
			Reflect.setValue(memb_holder, Mappings.net.minecraft.core.Holder.Reference.tags, memb_tags);
			contents.add(memb_holder);
		}
		Reflect.setValue(tag_members, Mappings.net.minecraft.core.HolderSet.Named.contents, contents);// Tag具有名称TagKey，因此一定是HolderSet.Named
		return tag_members;
	}

	/**
	 * 获取已经存在的HolderSet，通常为tag对应的成员列表
	 * 
	 * @param <T>          ResourceKey所属的类型，为NMS类型
	 * @param resource_key 要创建HolderSet的ResourceKey，可通过Registries获取
	 * @param tag_key      要获取HolderSet的TagKey
	 * @return 返回null或对应的HolderSet
	 */
	public static <T> HolderSet.Named<T> getTagMembers(ResourceKey<? extends IRegistry<T>> resource_key, TagKey<T> tag_key) {
		return ((IRegistry<T>) RegistryManager.getRegistry(resource_key)).getTag(tag_key).orElse(null);
	}

	public static <T> HolderSet.Named<T> getTagMembers(ResourceKey<? extends IRegistry<T>> resource_key, String namespaced_tag) {
		return getTagMembers(resource_key, getTagKey(resource_key, namespaced_tag));
	}

	/**
	 * 根据tag名称设置其下的成员列表。如果已经存在该tag则替换它
	 * 
	 * @param tag_name tag的名称
	 * @param members  该tag下的物品
	 * @return
	 */
	public static HolderSet.Named<Item> appendItemTagMembers(String namespaced_tag, Set<String> members) {
		return appendTagMembers(Registries.ITEM, namespaced_tag, members);
	}

	public static HolderSet.Named<Item> getItemTagMembers(TagKey<Item> tag_key) {
		return getTagMembers(Registries.ITEM, tag_key);
	}

	public static HolderSet.Named<Item> getItemTagMembers(String namespaced_tag) {
		return getTagMembers(Registries.ITEM, namespaced_tag);
	}

	/**
	 * 为一个Holder.Reference对象添加Tag
	 * 
	 * @param <T>
	 * @param resource_key     tag对应的ResourceKey
	 * @param tag_key          要添加的tag
	 * @param holder_reference 要添加tag的目标引用
	 * @return 返回是否添加成功
	 */
	public static <T> boolean addTag(TagKey<T> tag_key, Holder.c<T> holder_reference) {
		HolderSet.Named<T> holder_set;
		try {
			holder_set = RegistryManager.getRegistry(ResourceLocation.getResourceKey(tag_key)).getTag(tag_key).orElseThrow();
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Adding tag " + tag_key + " failed", ex);
			return false;
		}
		List<Holder<T>> contents = new ArrayList<>(HolderSetUtils.get_HolderSet_contents(holder_set));
		contents.add(holder_reference);
		return Reflect.setValue(holder_set, Mappings.net.minecraft.core.HolderSet.Named.contents, contents);
	}

	/**
	 * 为一个Holder.Reference引用批量添加tag
	 * 
	 * @param <T>
	 * @param tag_keys         TagKey集合
	 * @param holder_reference 待添加tag的引用
	 * @return 如果所有TagKey均成功添加则返回null，否则返回添加失败的TagKey集合
	 */
	public static <T> Set<TagKey<T>> addTag(Set<TagKey<T>> tag_keys, Holder.c<T> holder_reference) {
		Set<TagKey<T>> invalid_tags = new HashSet<>();
		for (TagKey<T> tag_key : tag_keys) {
			if (!addTag(tag_key, holder_reference))
				invalid_tags.add(tag_key);
		}
		return invalid_tags.isEmpty() ? null : invalid_tags;
	}

	/**
	 * 为一个引用批量添加同一种类型的tag
	 * 
	 * @param <T>
	 * @param tag_keys         tag集合
	 * @param holder_reference 待添加tag的引用
	 * @return 如果所有tag均成功添加则返回null，否则返回添加失败的tag集合
	 */
	public static <T> Set<String> addTag(ResourceKey<? extends IRegistry<T>> resource_key, Set<String> namespaced_tags, Holder.c<T> holder_reference) {
		Set<String> invalid_tags = new TreeSet<>();
		for (String namespaced_tag : namespaced_tags) {
			if (!addTag(resource_key, namespaced_tag, holder_reference))
				invalid_tags.add(namespaced_tag);
		}
		return invalid_tags.isEmpty() ? null : invalid_tags;
	}

	public static <T> boolean addTag(ResourceKey<? extends IRegistry<T>> resource_key, String namespaced_tag, Holder.c<T> holder_reference) {
		return addTag(getTagKey(resource_key, namespaced_tag), holder_reference);
	}

	/**
	 * 
	 * @param <T>
	 * @param resource_key     tag对应的ResourceKey
	 * @param tag_key          要移除的tag
	 * @param holder_reference 要移除tag的目标引用
	 * @return 返回是否移除成功
	 */
	public static <T> boolean removeTag(TagKey<T> tag_key, Holder.c<T> holder_reference) {
		HolderSet.Named<T> holder_set;
		try {
			holder_set = RegistryManager.getRegistry(ResourceLocation.getResourceKey(tag_key)).getTag(tag_key).orElseThrow();
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Adding tag " + tag_key + " failed", ex);
			return false;
		}
		List<Holder<T>> contents = new ArrayList<>(HolderSetUtils.get_HolderSet_contents(holder_set));
		contents.remove(holder_reference);
		return Reflect.setValue(holder_set, Mappings.net.minecraft.core.HolderSet.Named.contents, contents);
	}

	public static <T> boolean removeTag(ResourceKey<? extends IRegistry<T>> resource_key, String namespaced_tag, Holder.c<T> holder_reference) {
		return removeTag(getTagKey(resource_key, namespaced_tag), holder_reference);
	}
}
