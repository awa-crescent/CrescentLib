package lib.crescent.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import lib.crescent.enchantment.EnchantmentEntry;

public enum ArmorLevelType {// 盔甲附魔附魔的计算方式
	MAX, SUM;

	/**
	 * 按照指定方式计算指定位置的盔甲的附魔等级（最大或总和）
	 * 
	 * @param entity     要计算的实体
	 * @param calc_solts 计算的盔甲槽位，索引0-3分别是头、胸、裤、鞋
	 * @param level_type 计算方式，只取最大或计算等级之和
	 * @return 计算的附魔等级
	 */
	public static int calcArmorEnchantmentLevel(LivingEntity entity, String namespaced_enchantment, boolean[] calc_solts, ArmorLevelType level_type) {
		ItemStack[] armor_slots = entity.getEquipment().getArmorContents();
		Integer enchantment_lv = null;
		int lv = 0;
		for (int i = 0; i < armor_slots.length; ++i)
			if (calc_solts[i]) {
				if (armor_slots[i] == null)
					continue;
				enchantment_lv = EnchantmentEntry.getAllEnchantmentsAndLevels(armor_slots[i]).get(namespaced_enchantment);
				if (enchantment_lv == null)
					continue;
				switch (level_type) {
				case SUM:
					lv += enchantment_lv;
				case MAX:
					lv = enchantment_lv > lv ? enchantment_lv : lv;
				}
			}
		return lv;
	}
}