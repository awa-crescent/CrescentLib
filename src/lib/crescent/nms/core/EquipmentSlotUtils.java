package lib.crescent.nms.core;

import java.util.Locale;

public class EquipmentSlotUtils {
	public static net.minecraft.world.entity.EquipmentSlotGroup[] getNMSEquipmentSlotGroup(org.bukkit.inventory.EquipmentSlot[] slots) {
		net.minecraft.world.entity.EquipmentSlotGroup[] nms_slots = new net.minecraft.world.entity.EquipmentSlotGroup[slots.length];
		for (int idx = 0; idx < slots.length; idx++)
			nms_slots[idx] = net.minecraft.world.entity.EquipmentSlotGroup.valueOf(slots[idx].getGroup().toString().toUpperCase(Locale.ROOT));// CraftEquipmentSlot.getNMSGroup()展开
		return nms_slots;
	}

	@SuppressWarnings("deprecation")
	public static org.bukkit.inventory.EquipmentSlot[] fromNMSEquipmentSlotGroup(net.minecraft.world.entity.EquipmentSlotGroup[] slots) {
		org.bukkit.inventory.EquipmentSlot[] nms_slots = new org.bukkit.inventory.EquipmentSlot[slots.length];
		for (int idx = 0; idx < slots.length; idx++)
			nms_slots[idx] = org.bukkit.inventory.EquipmentSlotGroup.getByName(slots[idx].toString()).getExample();
		return nms_slots;
	}
}
