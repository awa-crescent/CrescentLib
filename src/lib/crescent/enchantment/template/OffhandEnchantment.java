package lib.crescent.enchantment.template;

import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import lib.crescent.enchantment.TriggerableEnchantment;
import lib.crescent.tag.Tag;

/**
 * 副手触发效果的附魔，默认是盾
 */
public class OffhandEnchantment extends TriggerableEnchantment {
	boolean shield_trigger = true;// 是否只有盾才能触发效果

	public OffhandEnchantment(String plugin_name, String namespace, String enchantment_id, String locale_key) {
		super(plugin_name, namespace, enchantment_id, locale_key);
		this.supported_items = Tag.SHIELD_ENCHANTABLE;
		this.primary_items = Tag.SHIELD_ENCHANTABLE;
		this.slots = new EquipmentSlot[] { EquipmentSlot.OFF_HAND };
	}

	@Override
	protected boolean checkItemTriggerable(ItemStack trigger_item) {
		return super.checkItemTriggerable(trigger_item) && (shield_trigger ? trigger_item.getType().equals(Material.SHIELD) : true);
	}

	@Override
	protected void setTriggerItem(EntityEquipment trigger_entitys_equipment) {
		trigger_item = trigger_entitys_equipment.getItemInOffHand();
	}
}
