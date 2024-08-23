package lib.crescent.enchantment.template;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

import lib.crescent.enchantment.TriggerableEnchantment;
import lib.crescent.tag.Tag;

/**
 * 主手触发的附魔效果
 */
public class MainhandEnchantment extends TriggerableEnchantment {

	public enum MainhandType {
		WEAPON, SHARP_WEAPON, SWORD, AXES, PICKAXES, SHOVELS, HOES, MINING, MINING_LOOT, FISHING, TRIDENT, BOW, CROSS_BOW, MACE
	}

	public MainhandEnchantment(String plugin_name, String namespace, String enchantment_id, String locale_key) {
		super(plugin_name, namespace, enchantment_id, locale_key);
		setMainhandType(MainhandType.WEAPON);
	}

	public final void setMainhandType(MainhandType type) {
		switch (type) {
		case AXES:
			this.supported_items = Tag.AXES;
			this.primary_items = Tag.AXES;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case BOW:
			this.supported_items = Tag.BOW_ENCHANTABLE;
			this.primary_items = Tag.BOW_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case CROSS_BOW:
			this.supported_items = Tag.CROSSBOW_ENCHANTABLE;
			this.primary_items = Tag.CROSSBOW_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case FISHING:
			this.supported_items = Tag.FISHING_ENCHANTABLE;
			this.primary_items = Tag.FISHING_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case HOES:
			this.supported_items = Tag.HOES;
			this.primary_items = Tag.HOES;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case MACE:
			this.supported_items = Tag.MACE_ENCHANTABLE;
			this.primary_items = Tag.MACE_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case MINING:
			this.supported_items = Tag.MINING_ENCHANTABLE;
			this.primary_items = Tag.MINING_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case MINING_LOOT:
			this.supported_items = Tag.MINING_LOOT_ENCHANTABLE;
			this.primary_items = Tag.MINING_LOOT_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case PICKAXES:
			this.supported_items = Tag.PICKAXES;
			this.primary_items = Tag.PICKAXES;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case SHARP_WEAPON:
			this.supported_items = Tag.SHARP_WEAPON_ENCHANTABLE;
			this.primary_items = Tag.SHARP_WEAPON_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case SHOVELS:
			this.supported_items = Tag.SHOVELS;
			this.primary_items = Tag.SHOVELS;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case SWORD:
			this.supported_items = Tag.SWORD_ENCHANTABLE;
			this.primary_items = Tag.SWORD_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case TRIDENT:
			this.supported_items = Tag.TRIDENT_ENCHANTABLE;
			this.primary_items = Tag.TRIDENT_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		case WEAPON:
			this.supported_items = Tag.WEAPON_ENCHANTABLE;
			this.primary_items = Tag.WEAPON_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
			break;
		default:
			this.supported_items = Tag.DURABILITY_ENCHANTABLE;
			this.primary_items = Tag.DURABILITY_ENCHANTABLE;
			this.slots = new EquipmentSlot[] { EquipmentSlot.HAND };
		}
	}

	@Override
	protected void setTriggerItem(EntityEquipment trigger_entitys_equipment) {
		trigger_item = trigger_entitys_equipment.getItemInMainHand();
	}
}
