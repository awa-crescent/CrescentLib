package lib.crescent.enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import lib.crescent.nms.enchantment.NMSEnchantment;
import lib.crescent.tag.Tag;
import lib.crescent.utils.format.FormattingStyle;
import lib.crescent.utils.locale.Locale;

//附魔类
public class EnchantmentEntry extends Enchantment implements Listener {
	public boolean use_nms = true;// 是否使用NMS的附魔进行注册，而非Spigot API
	protected FormattingStyle display_name_style;
	private String plugin_name;
	private String namespace;// 附魔的命名空间
	private String enchantment_id;// 附魔的id
	private String locale_key;// 本地化的key，将根据key查找对应语言文件的值作为附魔的显示名称
	protected boolean is_curse = false;// 是否是诅咒
	protected boolean is_treasure = false;// 是否是宝藏型附魔，如果是则不能通过附魔台获得，只能寻找箱子生成
	protected boolean is_tradeable = true;// 是否可交易，如果是则可以跟村民交易获取
	protected boolean is_discoverable = true;// 是否可以通过附魔台附魔，设置为false将不能通过附魔台附魔
	// 是否可以附魔台上直接看到该附魔，即确定该附魔是否为隐藏属性
	// NMS相关
	protected String[] effects;// 效果
	/**
	 * weight必须介于1-1024间，否则抛出下述无效参数异常 Failed to handle packet
	 * ServerboundSelectKnownPacks[knownPacks=[minecraft:core:1.21]], suppressing
	 * error java.lang.IllegalArgumentException: Failed to serialize
	 * ResourceKey[minecraft:enchantment / your_enchantment_namespace_and_id]: Value
	 * must be within range [1;1024]: invalid_weight_value
	 */
	private int weight = 1;
	protected int max_level;
	protected int min_level = 1;
	protected int[] min_cost;
	protected int[] max_cost;
	protected int anvil_cost = 0;
	protected TreeSet<String> exclusive_set;// 冲突附魔
	protected Tag supported_items;
	protected Tag primary_items;
	protected EquipmentSlot[] slots;// 装备在什么位置附魔才生效
	protected TreeSet<String> tags;// 附魔的tag，不可包含curse、treasure、non_treasure、tradeable、in_enchanting_table（如果有会被无视），因为这些tag是由is_curse、is_treasure、is_tradeable、is_hidden统一管理

	private boolean has_casted_nms = false;
	private NMSEnchantment nms_enchantment;

	/**
	 * 
	 * @param plugin_name    插件名称，用于命名空间
	 * @param enchantment_id 附魔id
	 * @param locale_key     本地化key，默认为plugin_name.enchantment_id
	 */
	public EnchantmentEntry(String plugin_name, String namespace, String enchantment_id, String locale_key) {
		this.plugin_name = plugin_name;
		this.enchantment_id = enchantment_id;
		this.namespace = namespace;
		this.locale_key = locale_key;
		this.tags = new TreeSet<String>();// 可排序，且不允许null
		this.display_name_style = new FormattingStyle();
		Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getServer().getPluginManager().getPlugin(plugin_name));// 注册该类的事件监听，注册后方可使用@EventHandler
	}

	public EnchantmentEntry(String plugin_name, String namespace, String enchantment_id) {
		this(plugin_name, namespace, enchantment_id, plugin_name + '.' + enchantment_id);
	}

	public final EnchantmentEntry setWeight(int weight) {
		if (weight < 1)
			this.weight = 1;
		else if (weight > 1024)
			this.weight = 1024;
		else
			this.weight = weight;
		return this;
	}

	/**
	 * 
	 * @return 附魔的本地化名称
	 */
	public final String getLocalName() {
		return Locale.getLocalizedValue(plugin_name, locale_key);
	}

	public final String getEnchantmentID() {
		return enchantment_id;
	}

	public final String getNamespace() {
		return namespace;
	}

	public final String getPluginName() {
		return plugin_name;
	}

	public final int getAnvilCost() {
		return anvil_cost;
	}

	public final int[] getMinCost() {
		return min_cost;
	}

	public final int[] getMaxCost() {
		return max_cost;
	}

	public final int getWeight() {
		return weight;
	}

	public final EquipmentSlot[] getSlots() {
		return slots;
	}

	public final Set<String> getExclusiveSet() {
		return exclusive_set;
	}

	public final Tag getSupportedItems() {
		return supported_items;
	}

	public final Tag getPrimaryItems() {
		return primary_items;
	}

	public final String[] getEffects() {
		return effects;
	}

	public final Set<String> getTags() {
		return tags;
	}

	public final FormattingStyle getDisplayNameStyle() {
		return display_name_style;
	}

	/**
	 * 返回物品具有的所有附魔和对应等级
	 * 
	 * @param item 要获取附魔的物品
	 * @return 命名空间:附魔id为key，附魔等级为value的HashMap
	 */
	public final static Map<String, Integer> getAllEnchantmentsAndLevels(ItemStack item) {
		if (item == null)
			return null;
		Map<String, Integer> entries = new HashMap<>();
		Set<Entry<Enchantment, Integer>> enchantments_set = item.getEnchantments().entrySet();
		for (Entry<Enchantment, Integer> entry : enchantments_set)
			entries.put(entry.getKey().getKey().toString(), entry.getValue());
		return entries;
	}

	/**
	 * 获取某个物品上该附魔的等级
	 * 
	 * @param item 要获取附魔等级的物品
	 * @return 附魔等级
	 */
	public final int getLevel(ItemStack item) {
		if (item == null)
			return 0;
		Integer lv = getAllEnchantmentsAndLevels(item).get(this.toString());
		return lv == null ? 0 : lv;
	}

	/**
	 * 经过该方法处理后才可使用tags，其操作包括将is_curse、is_treasure、is_tradeable、is_discoverable应用于tags
	 * 
	 * @return
	 */
	public EnchantmentEntry resolveTags() {
		if (is_curse)
			tags.add("curse");
		else
			tags.remove("curse");
		if (is_treasure) {
			tags.add("treasure");
			tags.remove("non_treasure");
		} else {
			tags.add("non_treasure");
			tags.remove("treasure");
		}
		if (is_tradeable)
			tags.add("tradeable");
		else
			tags.remove("tradeable");
		if (is_discoverable)
			tags.add("in_enchanting_table");
		else
			tags.remove("in_enchanting_table");
		return this;
	}

	public final boolean containsEnchantment(ItemStack item) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().hasEnchant(this);
	}

	public static boolean containsEnchantment(ItemStack item, EnchantmentEntry enchantment) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().hasEnchant(enchantment);
	}

	public final BukkitTask executeTask(Runnable task) {
		return Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin(plugin_name), task);
	}

	public final BukkitTask executeTask(Runnable task, long delay_ticks) {
		return Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(plugin_name), task, delay_ticks);
	}

	public final NMSEnchantment castToNMS() {
		if (!has_casted_nms) {
			nms_enchantment = NMSEnchantment.cast(this);
			has_casted_nms = true;
		}
		return nms_enchantment;
	}

	/**
	 * @param enchantment_level 附魔的等级
	 * @param cost              花费的等级
	 * @return 返回附魔台上显示的EnchantmentOffer，
	 */
	public EnchantmentOffer getEnchantmentOffer(int enchantment_level, int cost) {
		return new EnchantmentOffer(this, enchantment_level, cost);
	}

	@Override
	public final int getStartLevel() {
		return min_level;
	}

	@Override
	public final int getMaxLevel() {
		return max_level;
	}

	public final int getMinLevel() {
		return min_level;
	}

	@Override
	public final NamespacedKey getKey() {
		return new NamespacedKey(namespace, enchantment_id);
	}

	@Override
	public final String getTranslationKey() {
		return locale_key;
	}

	@Override
	public final boolean conflictsWith(Enchantment other) {
		return exclusive_set == null ? false : exclusive_set.contains(other.getKey().getKey());
	}

	@Override
	public final boolean canEnchantItem(ItemStack item) {
		return item.getAmount() == 1;// ItemStack只有一个物品时才允许附魔和铁砧加附魔
	}

	@Deprecated
	@Override
	public final EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
	}

	@Deprecated
	@Override
	public final String getName() {
		return enchantment_id;
	}

	/**
	 * @return 是否是诅咒类型附魔
	 */
	@Deprecated
	@Override
	public final boolean isCursed() {
		return is_curse;
	}

	/**
	 * @return 是否是宝藏类型附魔
	 */
	@Deprecated
	@Override
	public final boolean isTreasure() {
		return is_treasure;
	}

	@Override
	public final String toString() {
		return getKey().toString();
	}
}
