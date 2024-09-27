package lib.crescent.nms.enchantment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.enchantments.CraftEnchantment;
import org.bukkit.inventory.EquipmentSlot;

import lib.crescent.enchantment.EnchantmentEntry;
import lib.crescent.nms.NMSManipulator;
import lib.crescent.nms.ServerEntry;
import lib.crescent.nms.core.HolderSetUtils;
import lib.crescent.nms.core.RegistryManager;
import lib.crescent.nms.core.ResourceLocation;
import lib.crescent.nms.core.TagUtils;
import lib.crescent.tag.Tag;
import lib.crescent.utils.format.FormattingStyle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * NMS附魔类，作为不使用Spigot API的备用方案
 */
public class NMSEnchantment {
	protected final String namespace;// 附魔的命名空间，原版附魔的命名空间为minecraft
	protected final String enchantment_id;// 附魔的id
	protected final String local_name;// 显示名称，即附魔的本地化名称
	protected final FormattingStyle description_style;
	protected final NMSEnchantmentDefinition definition;

	protected final Enchantment enchantment;// 原始NMS附魔类
	private Holder.c<Enchantment> reference;// 附魔的引用net.minecraft.core.Holder$Reference
	private IChatBaseComponent description;// 显示名称组件net.minecraft.network.chat.Component
	private HashSet<TagKey<Enchantment>> tags;// 附魔的tag，包含是否是诅咒、是否是宝藏附魔、是否可交易等，通过net.minecraft.tags.EnchantmentTags获取
	private TreeSet<String> exclusive_set;// 冲突附魔，可以为null，表示无冲突附魔

	/**
	 * 注册表解冻之前不得调用
	 * 
	 * @param namespace                      附魔所示的命名空间
	 * @param enchantment_id                 附魔的文本id
	 * @param local_name                     附魔本地化名称，用于显示
	 * @param weight                         附魔所占权重，计算为本权重/所有附魔的权重和
	 * @param max_level                      附魔的最大等级
	 * @param min_cost                       附魔所需的最低经验等级，NMS对应类为net.minecraft.world.item.enchantment.Enchantment$Cost
	 * @param max_cost                       附魔所需的最高经验等级
	 * @param anvil_cost                     使用铁砧附魔所需的最低经验等级
	 * @param slots                          附魔装备在何位置生效
	 * @param tags                           附魔的tag
	 * @param exclusive_set                  冲突附魔集合
	 * @param effects                        附魔效果
	 * @param supported_items_namespaced_tag 支持通过铁砧、附魔台附魔这个附魔的物品tag名称
	 * @param supported_items                支持通过铁砧、附魔台附魔这个附魔的物品，设置为null则寻找已存在的supported_items_tag_name对应的物品集合
	 * @param primary_items_namespaced_tag   支持通过附魔台附魔这个附魔的物品tag名称
	 * @param primary_items                  支持通过附魔台附魔这个附魔的物品，应当是supported_items的子集。设置为null则寻找已存在的primary_items_tag_name对应的物品集合
	 */

	protected NMSEnchantment(String namespace, String enchantment_id, String local_name, FormattingStyle description_style, Tag supported_items, Tag primary_items, int weight, int min_level, int max_level, int[] min_cost, int[] max_cost, int anvil_cost, EquipmentSlot[] slots, Set<String> tags, Set<String> exclusive_set, String[] effects) {
		this.enchantment_id = enchantment_id;
		this.namespace = namespace == null ? "minecraft" : namespace;// 命名空间为空则默认为minecraft空间
		this.local_name = local_name;
		this.description_style = description_style;
		this.description = ServerEntry.getComponent(local_name, description_style);
		this.tags = tags == null ? new HashSet<TagKey<Enchantment>>() : getTagKeysFrom(tags);
		this.exclusive_set = exclusive_set == null || exclusive_set instanceof TreeSet ? (TreeSet<String>) exclusive_set : new TreeSet<String>(exclusive_set);
		DataComponentMap nms_effects = DataComponentMap.builder().build();
		// 附魔定义类net.minecraft.world.item.enchantment.Enchantment$EnchantmentDefinition未反混淆时为Enchantment.c
		this.definition = new NMSEnchantmentDefinition(supported_items, primary_items, weight, max_level, min_cost, max_cost, anvil_cost, slots);
		this.enchantment = new Enchantment(this.description, this.definition.castToNMSEnchantmentDefinition(), HolderSet.direct(), nms_effects);
		this.reference = RegistryManager.enchantment.createIntrusiveHolder(this.enchantment);
	}

	/**
	 * 根据附魔id获取对应的引用net.minecraft.core.Holder$Reference
	 * 
	 * @param namespace      附魔命名空间
	 * @param enchantment_id 附魔id
	 * @return 返回该id对应的附魔引用，即Holder$Reference，不存在则返回null
	 */
	public static Holder.c<Enchantment> getReference(String namespace, String enchantment_id) {
		return RegistryManager.enchantment.getHolder(ResourceLocation.getResourceKey(Registries.ENCHANTMENT, namespace, enchantment_id)).orElse(null);
	}

	/**
	 * 根据附魔id获取对应的引用net.minecraft.core.Holder$Reference
	 * 
	 * @param namespaced_enchantment_id 带命名空间的附魔id，用:隔开
	 * @return 返回该id对应的附魔引用，即Holder$Reference，不存在则返回null
	 */
	public static Holder.c<Enchantment> getReference(String namespaced_enchantment_id) {
		String[] namespace_id = ResourceLocation.parseNamespacedID(namespaced_enchantment_id);
		return RegistryManager.enchantment.getHolder(ResourceLocation.getResourceKey(Registries.ENCHANTMENT, namespace_id[0], namespace_id[1])).orElse(null);
	}

	/**
	 * 将String类型的tag集合转换为TagKey类型的集合
	 * 
	 * @param tags 待转换的tag集合
	 * @return TagKey类型的tag集合
	 */
	public static HashSet<TagKey<Enchantment>> getTagKeysFrom(Set<String> tags) {
		HashSet<TagKey<Enchantment>> set = new HashSet<TagKey<Enchantment>>();
		for (String tag : tags)
			set.add(TagUtils.getTagKey(Registries.ENCHANTMENT, tag));
		return set;
	}

	public NMSEnchantment addTag(TagKey<Enchantment> tag_key) {
		tags.add(tag_key);
		return this;
	}

	public NMSEnchantment removeTag(TagKey<Enchantment> tag_key) {
		tags.remove(tag_key);
		return this;
	}

	public NMSEnchantment addTag(String tag) {
		tags.add(TagUtils.getTagKey(Registries.ENCHANTMENT, tag));
		return this;
	}

	public NMSEnchantment removeTag(String tag) {
		tags.remove(TagUtils.getTagKey(Registries.ENCHANTMENT, tag));
		return this;
	}

	/**
	 * 当使用addTag()、removeTag()确定好该附魔的tag以后，使用该方法真正应用tag。此操作必须在注册NMS附魔以后使用
	 * 
	 * @return 全部tags均添加成功则返回true，否则返回false
	 */
	protected boolean applyTags() {
		Set<TagKey<Enchantment>> invalid_tags = TagUtils.addTag(tags, reference);
		if (invalid_tags != null) {
			Bukkit.getLogger().log(Level.SEVERE, "Invalid tags " + invalid_tags + " for enchantment " + enchantment_id);
			return false;
		}
		return true;
	}

	/**
	 * 使用该方法真正应用冲突附魔，此操作必须在注册完exclusive_set中的所有NMS附魔以后才可以使用
	 * 
	 * @return 应用冲突附魔成功则返回true，否则返回false
	 */
	public boolean applyExclusiveSet() {
		if (exclusive_set == null || exclusive_set.isEmpty())// 如果没有冲突集合则直接返回成功
			return true;
		if (enchantment == null || reference == null) {
			Bukkit.getLogger().log(Level.SEVERE, " Could not set exclusive set. Enchantment " + enchantment_id + " is not registered");
			return false;
		}
		return setExclusiveSetContents(enchantment, exclusive_set);
	}

	/**
	 * 根据附魔id获取对应的附魔net.minecraft.world.item.enchantment.Enchantment对象
	 * 
	 * @param namespaced_enchantment_id 附魔id，不带命名空间则是默认空间minecraft
	 * @return 返回该id对应的附魔对象，即Enchantment对象
	 */
	public static Enchantment getEnchantment(String namespaced_enchantment_id) {
		return RegistryManager.enchantment.get(ResourceLocation.getResourceKey(Registries.ENCHANTMENT, namespaced_enchantment_id));
	}

	public static HolderSet<Enchantment> getExclusiveSet(String namespaced_enchantment_id) {
		Enchantment enchantment = getEnchantment(namespaced_enchantment_id);
		if (enchantment == null) {
			Bukkit.getLogger().log(Level.SEVERE, " Could not get exclusive set. Enchantment " + namespaced_enchantment_id + " is not registered");
			return null;
		}
		return enchantment.exclusiveSet();
	}

	/**
	 * 为指定附魔设置冲突集合，其原理是修改Enchantment.exclusiveSet的contents成员。如果该附魔冲突集是HolderSet.Named则有可能游戏内表现行为不好，如果是HolderSet.Direct则表现符合预期。本库使用的冲突集均为HolderSet.Direct
	 * 
	 * @param enchantment
	 * @param exclusive_set
	 * @return
	 */
	public static boolean setExclusiveSetContents(Enchantment enchantment, Set<String> exclusive_set) {
		HolderSet<Enchantment> nms_exclusive_set = enchantment.exclusiveSet();
		List<Holder<Enchantment>> contents = new ArrayList<>();
		for (String en : exclusive_set) {
			Holder.c<Enchantment> ref = getReference(en);
			if (ref == null) {
				Bukkit.getLogger().log(Level.SEVERE, " Could not set exclusive set for " + enchantment.toString() + ". Exclusive enchantment " + en + " is not registered");
				continue;
			}
			contents.add(ref);
		}
		return HolderSetUtils.modify_HolderSet_contents(nms_exclusive_set, contents);
	}

	/**
	 * 为指定附魔设置冲突集合，其原理是新建一个HolderSet.Direct集合覆盖原有的Enchantment.exclusiveSet成员。如果setExclusiveSet()设置的冲突集合行为不好，则使用该函数设置冲突集合
	 * 
	 * @param enchantment
	 * @param exclusive_set
	 * @return
	 */
	public static boolean setExclusiveSetDirect(Enchantment enchantment, Set<String> exclusive_set) {
		return set_Enchantment_exclusiveSet(enchantment, HolderSetUtils.createHolderSetDirect(Registries.ENCHANTMENT, exclusive_set));
	}

	/**
	 * 设置冲突附魔，此操作必须在注册NMS附魔以后使用
	 * 
	 * @return 设置冲突附魔成功则返回true，否则返回false
	 */
	public static boolean setExclusiveSetContents(String namespaced_enchantment_id, Set<String> exclusive_set) {
		return setExclusiveSetContents(getEnchantment(namespaced_enchantment_id), exclusive_set);
	}

	/**
	 * 添加指定的冲突附魔，此操作必须在注册NMS附魔以后使用
	 * 
	 * @return 添加冲突附魔成功则返回true，否则返回false
	 */
	@SuppressWarnings("unchecked")
	public static boolean appendExclusiveSet(String namespaced_enchantment_id, Set<String> exclusive_set) {
		if (exclusive_set == null || exclusive_set.isEmpty())// 如果待添加的冲突集合为空则直接返回成功
			return true;
		HolderSet<Enchantment> nms_exclusive_set = getExclusiveSet(namespaced_enchantment_id);
		List<Holder<Enchantment>> contents = new ArrayList<>((List<Holder<Enchantment>>) NMSManipulator.access(nms_exclusive_set, "net.minecraft.core.HolderSet$Named.contents"));
		for (String en : exclusive_set)
			contents.add(getReference(en));
		return HolderSetUtils.modify_HolderSet_contents(nms_exclusive_set, contents);
	}

	/**
	 * 移除指定的冲突附魔，此操作必须在注册NMS附魔以后使用
	 * 
	 * @return 移除冲突附魔成功则返回true，否则返回false
	 */
	@SuppressWarnings("unchecked")
	public static boolean removeExclusiveSet(String namespaced_enchantment_id, Set<String> exclusive_set) {
		if (exclusive_set == null || exclusive_set.isEmpty())// 如果待添加的冲突集合为空则直接返回成功
			return true;
		HolderSet<Enchantment> nms_exclusive_set = getExclusiveSet(namespaced_enchantment_id);
		List<Holder<Enchantment>> contents = new ArrayList<>((List<Holder<Enchantment>>) NMSManipulator.access(nms_exclusive_set, "net.minecraft.core.HolderSet$Named.contents"));
		for (String en : exclusive_set)
			contents.remove(getReference(en));
		return HolderSetUtils.modify_HolderSet_contents(nms_exclusive_set, contents);
	}

	/**
	 * 移除所有冲突附魔，此操作必须在注册NMS附魔以后使用
	 * 
	 * @return 移除冲突附魔成功则返回true，否则返回false
	 */
	public static boolean removeExclusiveSet(String namespaced_enchantment_id) {
		return setExclusiveSetDirect(getEnchantment(namespaced_enchantment_id), Set.of());
	}

	/**
	 * 
	 * @param enchantment 将EnchantmentEntry转换为NMSEnchantment
	 * @return
	 */
	public static NMSEnchantment cast(EnchantmentEntry enchantment) {
		return new NMSEnchantment(enchantment.getNamespace(), enchantment.getEnchantmentID(), enchantment.getLocalName(), enchantment.getDisplayNameStyle(), enchantment.getSupportedItems(), enchantment.getPrimaryItems(), enchantment.getWeight(), enchantment.getMinLevel(), enchantment.getMaxLevel(), enchantment.getMinCost(), enchantment.getMaxCost(), enchantment.getAnvilCost(), enchantment.getSlots(), enchantment.resolveTags().getTags(), enchantment.getExclusiveSet(), enchantment.getEffects());
	}

	/**
	 * 将Spigot API的Enchantment转换为NMS的Enchantment
	 * 
	 * @param spigot_enchantment Spigot API提供的Enchantment
	 * @return NMS的Enchantment
	 */
	public static Enchantment cast(org.bukkit.enchantments.Enchantment spigot_enchantment) {
		return CraftEnchantment.bukkitToMinecraft(spigot_enchantment);
	}

	public static Set<Enchantment> cast(Set<org.bukkit.enchantments.Enchantment> spigot_enchantments) {
		Set<Enchantment> set = new HashSet<Enchantment>();
		for (org.bukkit.enchantments.Enchantment e : spigot_enchantments)
			set.add(CraftEnchantment.bukkitToMinecraft(e));
		return set;
	}

	/**
	 * 注册该附魔并完成注册后的动作，包括修改tag、冲突附魔
	 */
	public void register() {
		IRegistry.register(RegistryManager.enchantment, namespace + ':' + enchantment_id, enchantment);
		// 注册后才能修改tag和冲突附魔
		applyTags();
	}

	// 以下是修改Enchantment成员的工具函数
	@SuppressWarnings("unchecked")
	public static HolderSet<Enchantment> get_Enchantment_exclusiveSet(Enchantment enchantment) {
		return (HolderSet<Enchantment>) NMSManipulator.access(enchantment, "net.minecraft.world.item.enchantment.Enchantment.exclusiveSet");
	}

	public static boolean set_Enchantment_exclusiveSet(Enchantment enchantment, HolderSet<Enchantment> exclusive_set) {
		return NMSManipulator.setObjectValue(enchantment, "net.minecraft.world.item.enchantment.Enchantment.exclusiveSet", exclusive_set);
	}

	public static Enchantment.c get_Enchantment_definition(Enchantment enchantment) {
		return (Enchantment.c) NMSManipulator.access(enchantment, "net.minecraft.world.item.enchantment.Enchantment.definition");
	}

	public static boolean set_Enchantment_definition(Enchantment enchantment, Enchantment.c definition) {
		return NMSManipulator.setObjectValue(enchantment, "net.minecraft.world.item.enchantment.Enchantment.definition", definition);
	}
}