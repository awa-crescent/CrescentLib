package lib.crescent.nms;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;

import lib.crescent.Manipulator;
import lib.crescent.Reflect;

public abstract class CraftBukkitEntry {
	private static final String craftbukkit_package;

	static {
		craftbukkit_package = Bukkit.getServer().getClass().getPackage().getName();
	}

	/**
	 * 获取CraftBukkit的类
	 * 
	 * @param craftbukkit_name 从NMS版本号开始的类名称（包含包名）
	 * @return 目标类的Class。例如在1.21.0版本，传入enchantments.CraftEnchantment，可以返回Class<org.bukkit.craftbukkit.v1_21_R1.enchantments.CraftEnchantment>
	 */
	public static final Class<?> getCraftBukkitClass(String craftbukkit_name) {
		return Reflect.getClassForName(craftbukkit_package + '.' + craftbukkit_name);
	}

	public static final Class<?> getCraftBukkitClass(Object bukkit_obj) {
		return Reflect.getClassForName(getCraftBukkitClassName(bukkit_obj));
	}

	public static final String getCraftBukkitClassName(String bukkit_class_name) {
		if (!bukkit_class_name.contains("org.bukkit."))// 不是org.bukkit下的类则无对应CraftBukkit类
			return null;
		String craftbukkit_class_name = "Craft" + Reflect.getClassNameWithoutPackage(bukkit_class_name);// 类名称加上Craft就是对应的CraftBukkit类名称
		String craftbukkit_class_pkg = craftbukkit_package + '.' + Reflect.getPackageName(bukkit_class_name).substring("org.bukkit.".length());// 去除org.bukkit包，更换为org.bukkit.craftbukkit.vxxx
		System.out.println("Orig: " + bukkit_class_name + ", new: " + craftbukkit_class_pkg + '.' + craftbukkit_class_name);
		return craftbukkit_class_pkg + '.' + craftbukkit_class_name;
	}

	public static final String getCraftBukkitClassName(Object bukkit_obj) {
		return getCraftBukkitClassName(bukkit_obj.getClass().getName());
	}

	/**
	 * 将Bukkit对象转换为对应NMS版本的CraftBukkit对象
	 * 
	 * @param bukkit_obj
	 * @param craftbukkit_name
	 * @return
	 */
	public static final Object cast(Object bukkit_obj, String craftbukkit_name) {
		return getCraftBukkitClass(craftbukkit_name).cast(bukkit_obj);
	}

	public static final Object cast(Object bukkit_obj) {
		return getCraftBukkitClass(bukkit_obj).cast(bukkit_obj);
	}

	public static final <B extends Keyed> Object bukkitToMinecraft(B bukkit_obj) {
		return Manipulator.invoke(getCraftBukkitClass("Registry"), "bukkitToMinecraft", new Class<?>[] { bukkit_obj.getClass() }, bukkit_obj);
	}
}
