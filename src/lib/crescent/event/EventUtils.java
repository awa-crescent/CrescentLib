package lib.crescent.event;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import lib.crescent.Manipulator;
import lib.crescent.VMEntry;

public class EventUtils {
	/**
	 * 为一个对象本身及其成员字段、成员字段的成员字段...递归地注册所有Listener对象，
	 * 
	 * @param obj                    要注册的对象
	 * @param plugin                 注册的插件
	 * @param register_static_member 是否忽略静态成员
	 */
	public static void registerListenerWithMemberFieldRecursively(Object obj, Plugin plugin, boolean register_static_member) {
		if (obj == null || VMEntry.isPrimitiveBoxingType(obj))
			return;
		if (obj instanceof Listener listener)
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		Field[] fields = Manipulator.getDeclaredFields(obj.getClass());
		for (Field field : fields)
			try {
				if (!Modifier.isStatic(field.getModifiers()) || register_static_member) {
					registerListenerWithMemberFieldRecursively(Manipulator.removeAccessCheck(field).get(obj), plugin, register_static_member);
					Manipulator.recoveryAccessCheck(field);
				}
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
	}
}
