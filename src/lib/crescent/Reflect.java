package lib.crescent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;

/**
 * 
 * 反射工具
 *
 * @param <T> 目标类
 */
public abstract class Reflect {

	public static Class<?> getClassForName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询类成员，如果该类没有则递归查找父类
	 */
	public static Field getField(Object obj, String name) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		try {
			return cls.getDeclaredField(name);
		} catch (NoSuchFieldException ex) {
			Class<?> supercls = cls.getSuperclass();
			if (supercls == null) {
				System.err.println("Cannot find field " + name);
				ex.printStackTrace();
				return null;
			} else
				return getField(supercls, name);
		}
	}

	public static Object getValue(Object obj, Field field) {
		if (obj == null || field == null)
			return null;
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Reflection Utils reached IllegalAccessException reading field " + field, ex);
		}
		return null;
	}

	public static Object getValue(Object obj, String field) {
		return getValue(obj, getField(obj, field));
	}

	public static boolean setValue(Object obj, Field field, Object value) {
		if (obj == null || field == null)
			return false;
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalAccessException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Reflection Utils reached IllegalAccessException writing field " + field + " with value " + value + " in object " + obj.toString(), ex);
			return false;
		}
		return true;
	}

	public static boolean setValue(Object obj, String field, Object value) {
		return setValue(obj, getField(obj, field), value);
	}

	public static Method getMethod(Object obj, String name, Class<?>... arg_types) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		try {
			return cls.getDeclaredMethod(name, arg_types == null ? (new Class<?>[] {}) : arg_types);
		} catch (NoSuchMethodException ex) {
			Class<?> supercls = cls.getSuperclass();
			if (supercls == null) {
				System.err.println("Cannot find method " + name);
				ex.printStackTrace();
				return null;
			} else
				return getMethod(supercls, name, arg_types);
		}
	}

	public static void resolveInheritChain(Class<?> clazz, ArrayList<Class<?>> chain) {
		chain.add(clazz);
		Class<?> supercls = clazz.getSuperclass();
		if (supercls != null)
			resolveInheritChain(supercls, chain);
	}

	public static Class<?>[] resolveInheritChain(Class<?> clazz) {
		ArrayList<Class<?>> chain = new ArrayList<>();
		resolveInheritChain(clazz, chain);
		return (Class<?>[]) chain.toArray();
	}

	/**
	 * 推断每个参数的类型，每个参数的类型均是一个数组，为该类型的继承链
	 * 
	 * @param args 要推断的参数列表
	 * @return
	 */
	public static Class<?>[][] resolveArgTypesChain(Object... args) {
		Class<?>[][] arg_types = new Class<?>[args.length][];
		for (int idx = 0; idx < args.length; ++idx)
			arg_types[idx] = resolveInheritChain(args[idx].getClass());
		return arg_types;
	}

	/**
	 * 推断每个参数的类型，每个参数的类型均是传入参数本类型，不包括其父类继承链
	 * 
	 * @param args 要推断的参数列表
	 * @return
	 */
	public static Class<?>[] resolveArgTypes(Object... args) {
		Class<?>[] arg_types = new Class<?>[args.length];
		for (int idx = 0; idx < args.length; ++idx)
			arg_types[idx] = args[idx].getClass();
		return arg_types;
	}

	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		Method method = getMethod(obj, method_name, arg_types);
		try {
			method.setAccessible(true);
			return method.invoke(obj, args);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Reflection Utils reached exception invoking method " + method_name + " with arguments " + args + " in object " + obj.toString(), ex);
			return null;
		}
	}

	public static Constructor<?> getConstructor(Object obj, Class<?>... arg_types) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		try {
			return cls.getDeclaredConstructor(arg_types == null ? (new Class<?>[] {}) : arg_types);
		} catch (NoSuchMethodException ex) {
			Class<?> supercls = cls.getSuperclass();
			return supercls == null ? null : getConstructor(supercls, arg_types);
		}
	}

	/**
	 * 利用反射调用构造函数
	 * 
	 * @param obj
	 * @param args
	 * 
	 * @return
	 */
	public static Object construct(Object obj, Class<?> arg_types, Object... args) {
		Constructor<?> constructor = getConstructor(obj, arg_types);
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Reflection Utils reached exception contructing " + obj.toString() + " with arguments " + args, ex);
			return null;
		}
	}

	/**
	 * 判断某个类是否具有指定超类，支持向上递归查找超类
	 * 
	 * @param clazz       要判断是否有超类的类
	 * @param super_class 超类
	 * @return clazz具有超类super_class则返回true，否则返回false
	 */
	public static boolean hasSuperClass(Class<?> clazz, Class<?> super_class) {
		Class<?> supercls = clazz.getSuperclass();
		if (supercls == super_class)
			return true;
		return supercls == null ? false : hasSuperClass(supercls, super_class);
	}
}
