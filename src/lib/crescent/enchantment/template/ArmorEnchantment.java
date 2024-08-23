package lib.crescent.enchantment.template;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import lib.crescent.enchantment.TriggerableEnchantment;
import lib.crescent.entity.ArmorLevelType;
import lib.crescent.tag.Tag;

/**
 * 盔甲触发的附魔效果。默认附魔等级是四件的该附魔等级之和
 */
public class ArmorEnchantment extends TriggerableEnchantment {

	public enum ArmorType {
		ARMOR, HEAD, CHEST, LEG, FOOT, EQUIPPABLE
	}

	private ArmorType armor_type;
	protected ArmorLevelType level_type = ArmorLevelType.SUM;
	private boolean[] calc_solts;// 计算等级的槽位，应当与this.slots保持一致。固定顺序为鞋、裤、胸、头

	public ArmorEnchantment(String plugin_name, String namespace, String enchantment_id, String locale_key) {
		super(plugin_name, namespace, enchantment_id, locale_key);
		setArmorType(ArmorType.ARMOR);
	}

	/**
	 * 设置该附魔的类型，设置为ALL则四件盔甲均可附魔生效
	 * 
	 * @param armor_type
	 */
	public final void setArmorType(ArmorType armor_type) {
		this.armor_type = armor_type;
		switch (armor_type) {
		case ARMOR:
			this.supported_items = Tag.ARMOR_ENCHANTABLE;
			this.primary_items = Tag.ARMOR_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
			this.calc_solts = new boolean[] { true, true, true, true };
			break;
		case HEAD:
			this.supported_items = Tag.HEAD_ARMOR_ENCHANTABLE;
			this.primary_items = Tag.HEAD_ARMOR_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HEAD };
			this.calc_solts = new boolean[] { false, false, false, true };
			break;
		case CHEST:
			this.supported_items = Tag.CHEST_ARMOR_ENCHANTABLE;
			this.primary_items = Tag.CHEST_ARMOR_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.CHEST };
			this.calc_solts = new boolean[] { false, false, true, false };
			break;
		case LEG:
			this.supported_items = Tag.LEG_ARMOR_ENCHANTABLE;
			this.primary_items = Tag.LEG_ARMOR_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.LEGS };
			this.calc_solts = new boolean[] { false, true, false, false };
			break;
		case FOOT:
			this.supported_items = Tag.FOOT_ARMOR_ENCHANTABLE;
			this.primary_items = Tag.FOOT_ARMOR_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.FEET };
			this.calc_solts = new boolean[] { true, false, false, false };
			break;
		case EQUIPPABLE:
			this.supported_items = Tag.EQUIPPABLE_ENCHANTABLE;
			this.primary_items = Tag.EQUIPPABLE_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
			this.calc_solts = new boolean[] { true, true, true, true };
			break;
		}
	}

	public final ArmorType getArmorType() {
		return armor_type;
	}

	@Override
	public int getLevel() {// 不单独设置触发物品，则按照level_type计算所有烂尾的附魔等级
		return trigger_item == null ? ArmorLevelType.calcArmorEnchantmentLevel(trigger_entity, this.toString(), calc_solts, level_type) : super.getLevel();
	}

	@Override
	public boolean checkItemTriggerable(ItemStack trigger_item) {
		return this.getLevel() > 0;
	}

	/**
	 * 不设置触发物品就是四个栏位的Armor都能触发
	 */
	@Override
	protected void setTriggerItem(EntityEquipment trigger_entitys_equipment) {

	}
}
