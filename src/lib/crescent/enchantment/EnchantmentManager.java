/**
 * 实现基于https://www.spigotmc.org/threads/1-21-register-custom-enchantments.651347/
 */

package lib.crescent.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import lib.crescent.nms.core.RegistryManager;
import lib.crescent.utils.reflex.JarUtils;
import net.minecraft.core.registries.Registries;

@SuppressWarnings("deprecation")
public final class EnchantmentManager implements Listener {
	public static boolean can_enchant_stack = false;// 设置是否可以附魔堆叠物品
	public static ArrayList<Set<String>> enchantable_items_set = new ArrayList<>();// 需要添加可附魔的新物品

	public final static HashMap<String, EnchantmentEntry> registered_enchantments = new HashMap<>();// 注册的所有EnchantmentEntry附魔Map，key为带命名空间的id，例如"minecraft:sharpness"

	/**
	 * 注册附魔并应用tags和冲突集合
	 * 
	 * @param enchantment 要注册的附魔
	 */
	public static void register(EnchantmentEntry enchantment) {
		if (enchantment.use_nms) {
			if (RegistryManager.isFrozen(Registries.ENCHANTMENT))
				RegistryManager.unfreezeRegistry(Registries.ENCHANTMENT);
			enchantment.castToNMS().register();
		} else {

		}
	}

	/**
	 * 注册Java包下的所有类附魔并应用tags和冲突集合
	 * 
	 * @param plugin               附魔类所属的插件，每个插件都有自己的ClassLoader因此必须传入以供库函数访问
	 * @param enchantments_package 要注册的附魔类所在的Java包
	 */

	public static void registerAll(Plugin plugin, String enchantments_package, boolean include_subpackage) {
		List<Class<?>> enchantment_class_list = JarUtils.getSubClassInJarPackage(plugin.getClass(), enchantments_package, EnchantmentEntry.class, include_subpackage);
		if (enchantment_class_list == null) {
			Bukkit.getLogger().log(Level.WARNING, "EnchantmentManager didn't find any class extends " + EnchantmentEntry.class.getName() + " in package " + enchantments_package);
			return;
		}
		for (Class<?> enchantment_class : enchantment_class_list) {// 注册所有附魔
			try {
				EnchantmentEntry enchantment = (EnchantmentEntry) (enchantment_class.newInstance());
				register(enchantment);
				registered_enchantments.put(enchantment.getNamespace() + ':' + enchantment.getEnchantmentID(), enchantment);
			} catch (InstantiationException e) {
				Bukkit.getLogger().log(Level.SEVERE, "EnchantmentManager cannot create instance for " + enchantment_class);
			} catch (IllegalAccessException e) {
				Bukkit.getLogger().log(Level.SEVERE, "EnchantmentManager cannot access class " + enchantment_class + ". Check its constructor's access level");
			}
		}
		for (Map.Entry<String, EnchantmentEntry> enchantment_entry : registered_enchantments.entrySet()) {// 注册完毕后，给所有附魔应用冲突附魔
			EnchantmentEntry enchantment = enchantment_entry.getValue();
			if (enchantment.use_nms)
				enchantment.castToNMS().applyExclusiveSet();
		}
		flush();
	}

	public static void registerAll(Plugin plugin, String enchantments_package) {
		registerAll(plugin, enchantments_package, false);
	}

	/**
	 * 所有附魔注册完毕后应当调用该方法以应用更改
	 */
	public static void flush() {
		RegistryManager.freezeRegistry(Registries.ENCHANTMENT);
	}

	@EventHandler
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {
		ItemStack item = event.getItem();
		if (item.getAmount() > 1 && !can_enchant_stack) {// 堆叠数量大于1的物品不可附魔
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onEnchantItemEvent(EnchantItemEvent event) {

	}
}
