package lib.crescent.nms.enchantment;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;

import lib.crescent.nms.core.EquipmentSlotUtils;
import lib.crescent.nms.core.HolderSetUtils;
import lib.crescent.nms.core.TagUtils;
import lib.crescent.tag.Tag;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class NMSEnchantmentDefinition {
	protected Tag supported_items;
	protected Tag primary_items;
	protected int weight;
	protected int max_level;
	protected int[] min_cost;
	protected int[] max_cost;
	protected int anvil_cost = 0;
	protected EquipmentSlot[] slots;

	public NMSEnchantmentDefinition(Tag supported_items, Tag primary_items, int weight, int max_level, int[] min_cost, int[] max_cost, int anvil_cost, EquipmentSlot[] slots) {
		this.supported_items = supported_items;
		this.primary_items = primary_items;
		this.weight = weight;
		this.max_level = max_level;
		this.min_cost = min_cost;
		this.max_cost = max_cost;
		this.anvil_cost = anvil_cost;
		this.slots = slots;
	}

	public Enchantment.c castToNMSEnchantmentDefinition() {
		HolderSet<Item> nms_supported_items = null;
		if (supported_items == null) {// 不设置成员则认为是该tag已经存在且有成员
			if ((nms_supported_items = TagUtils.getItemTagMembers(supported_items.getNamespacedTag())) == null)// 寻找tag的成员
				Bukkit.getLogger().log(Level.SEVERE, "Cannot find supported_items. Its tag " + supported_items + " doesn't exist");// 找不到就报错
		} else {
			if (supported_items.getNamespacedTag() == null)// 如果不指定Tag名称，则使用HolderSet.Direct，这是无Tag只包含成员的集合
				nms_supported_items = HolderSetUtils.createItemSet(supported_items.getMembers());
		}
		HolderSet<Item> nms_primary_items = null;
		if (primary_items == null) {// 不设置成员则认为是该tag已经存在且有成员
			if ((nms_primary_items = TagUtils.getItemTagMembers(primary_items.getNamespacedTag())) == null)// 寻找tag的成员
				Bukkit.getLogger().log(Level.SEVERE, "Cannot find primary_items. Its tag " + supported_items + " doesn't exist");
		} else {
			if (primary_items.getNamespacedTag() == null)
				nms_primary_items = HolderSetUtils.createItemSet(primary_items.getMembers());
		} // 找不到就新建
		EquipmentSlotGroup[] nms_slots = EquipmentSlotUtils.getNMSEquipmentSlotGroup(slots);
		return Enchantment.definition(nms_supported_items, nms_primary_items, weight, max_level, getNMSCost(min_cost), getNMSCost(max_cost), anvil_cost, nms_slots);
	}

	public NMSEnchantmentDefinition(Enchantment.c nms_definition) {
		this.supported_items = HolderSetUtils.toTag(nms_definition.supportedItems());
		this.primary_items = HolderSetUtils.toTag(nms_definition.primaryItems().orElse(null));
		this.weight = nms_definition.weight();
		this.max_level = nms_definition.maxLevel();
		this.min_cost = fromNMSCost(nms_definition.minCost());
		this.max_cost = fromNMSCost(nms_definition.maxCost());
		this.anvil_cost = nms_definition.anvilCost();
		this.slots = EquipmentSlotUtils.fromNMSEquipmentSlotGroup((EquipmentSlotGroup[]) nms_definition.slots().toArray());
	}

	public static NMSEnchantmentDefinition fromEnchantment(Enchantment enchantment) {
		Enchantment.c nms_definition = NMSEnchantment.get_Enchantment_definition(enchantment);
		NMSEnchantmentDefinition definition = new NMSEnchantmentDefinition(nms_definition);
		return definition;
	}

	/**
	 * 
	 * @param cost 附魔花费经验的二元数组，为Ⅰ级所需经验等级和每增加一级附魔等级所需经验等级
	 * @return NMS附魔花费对象
	 */
	public static Enchantment.b getNMSCost(int[] cost) {
		return new Enchantment.b(cost[0], cost[1]);
	}

	public static Enchantment.b getNMSCost(int cost) {
		return new Enchantment.b(cost, 0);
	}

	public static int[] fromNMSCost(Enchantment.b cost) {
		return new int[] { cost.base(), cost.perLevelAboveFirst() };
	}
}
