package lib.crescent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sun.misc.Unsafe;

@SuppressWarnings("deprecation")
public abstract class Manipulator {
	private static Class<?> internalUnsafeClazz;
	private static Unsafe unsafe;
	private static Object internalUnsafe;

	private static Method objectFieldOffset;// 没有检查的Unsafe.objectFieldOffset
	private static Method staticFieldBase;
	private static Method staticFieldOffset;
	/*
	 * 64位JVM的offset从12开始为数据段，此处为java.lang.reflect.AccessibleObject的boolean
	 * override成员，将该成员覆写为true可以无视权限调用Method、Field、Constructor
	 */
	private static long java_lang_reflect_AccessibleObject_override_OFFSET = 12;

	private static Class<?> SharedSecrets;// jdk.internal.access.SharedSecrets
	private static Object JavaLangAccess;// jdk.internal.access.JavaLangAccess;
	private static Method getConstantPool;// 获取指定类的类常量池The ConstantPool，其中包含静态成员、方法列表等

	private static VarHandle ReflectionFieldFilterMap;
	private static VarHandle ReflectionMethodFilterMap;

	private static MethodHandle getDeclaredFields0;// Class.getDeclaredFields0无视反射访问权限获取字段
	private static MethodHandle getDeclaredMethods0;
	private static MethodHandle searchFields;
	private static MethodHandle searchMethods;

	static {
		Field theUnsafe;
		Field theInternalUnsafe;
		try {
			theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (Unsafe) theUnsafe.get(null);
			internalUnsafeClazz = Class.forName("jdk.internal.misc.Unsafe");
			theInternalUnsafe = Unsafe.class.getDeclaredField("theInternalUnsafe");
			theInternalUnsafe.setAccessible(true);
			internalUnsafe = theInternalUnsafe.get(null);
			// 最优先获取java.lang.reflect.AccessibleObject的override以获取访问权限
			java_lang_reflect_AccessibleObject_override_OFFSET = unsafe.objectFieldOffset(BlankMirror_java_lang_reflect_AccessibleObject.class.getDeclaredField("override"));
			getDeclaredFields0 = Handle.findSpecialMethodHandle(Class.class, Class.class, "getDeclaredFields0", Field[].class, boolean.class);
			getDeclaredMethods0 = Handle.findSpecialMethodHandle(Class.class, Class.class, "getDeclaredMethods0", Method[].class, boolean.class);
			searchFields = Handle.findStaticMethodHandle(Class.class, "searchFields", Field.class, Field[].class, String.class);
			searchMethods = Handle.findStaticMethodHandle(Class.class, "searchMethods", Method.class, Method[].class, String.class, Class[].class);
			objectFieldOffset = removeAccessCheck(internalUnsafeClazz.getDeclaredMethod("objectFieldOffset", Field.class));
			staticFieldBase = removeAccessCheck(internalUnsafeClazz.getDeclaredMethod("staticFieldBase", Field.class));
			staticFieldOffset = removeAccessCheck(internalUnsafeClazz.getDeclaredMethod("staticFieldOffset", Field.class));
			SharedSecrets = Class.forName("jdk.internal.access.SharedSecrets");
			JavaLangAccess = getAccess("JavaLangAccess");
			getConstantPool = removeAccessCheck(JavaLangAccess.getClass().getDeclaredMethod("getConstantPool", Class.class));
			Class<?> Reflection = Class.forName("jdk.internal.reflect.Reflection");
			ReflectionFieldFilterMap = Handle.findStaticVarHandle(Reflection, "fieldFilterMap", Map.class);
			ReflectionMethodFilterMap = Handle.findStaticVarHandle(Reflection, "methodFilterMap", Map.class);
		} catch (NoSuchFieldException | NoSuchMethodException | ClassNotFoundException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取对象定义的字段原对象，无视反射过滤和访问权限，直接调用JVM内部的native方法获取全部字段
	 * 注意：本方法没有拷贝对象，因此对返回字段的任何修改都将反应在反射系统获取的所有的复制对象中
	 * 
	 * @param clazz 要获取的类
	 * @return 字段列表
	 */
	public static Field[] getDeclaredFields(Class<?> clazz) {
		try {
			return (Field[]) getDeclaredFields0.invokeExact(clazz, false);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Field getDeclaredField(Class<?> clazz, String field_name) {
		try {
			return (Field) searchFields.invokeExact(getDeclaredFields(clazz), field_name);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取对象定义的方法原对象，无视反射过滤和访问权限，直接调用JVM内部的native方法获取全部方法
	 * 
	 * @param clazz 要获取的类
	 * @return 字段列表
	 */
	public static Field[] getDeclaredMethods(Class<?> clazz) {
		try {
			return (Field[]) getDeclaredMethods0.invokeExact(clazz, false);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String method_name, Class<?> arg_types) {
		try {
			return (Method) searchMethods.invokeExact(getDeclaredMethods(clazz), method_name, arg_types);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取反射过滤的字段
	 * 
	 * @return
	 */
	public static Map<Class<?>, Set<String>> getReflectionFieldFilter() {
		return (Map<Class<?>, Set<String>>) ReflectionFieldFilterMap.get();
	}

	/**
	 * 获取反射过滤的方法
	 * 
	 * @return
	 */
	public static Map<Class<?>, Set<String>> getReflectionMethodFilter() {
		return (Map<Class<?>, Set<String>>) ReflectionMethodFilterMap.get();
	}

	/**
	 * 设置字段反射过滤，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会替换原有的过滤限制。危险操作。
	 */
	public static void setReflectionFieldFilter(Map<Class<?>, Set<String>> filter_map) {
		ReflectionFieldFilterMap.set(filter_map);
	}

	/**
	 * 设置方法反射过滤，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会替换原有的过滤限制。危险操作。
	 */
	public static void setReflectionMethodFilter(Map<Class<?>, Set<String>> filter_map) {
		ReflectionMethodFilterMap.set(filter_map);
	}

	/**
	 * 移除反射过滤，使得全部字段均可通过反射获取，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会移除该限制。危险操作。
	 */
	public static void removeReflectionFieldFilter() {
		setReflectionFieldFilter(new HashMap<Class<?>, Set<String>>());
	}

	/**
	 * 移除反射过滤，使得全部方法均可通过反射获取，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会移除该限制。危险操作。
	 */
	public static void removeReflectionMethodFilter() {
		setReflectionMethodFilter(new HashMap<Class<?>, Set<String>>());
	}

	/**
	 * 反射工具
	 */

	/**
	 * 本类最核心的方法，移除AccessibleObject的访问安全检查限制，使得对象可以被访问
	 * 
	 * @param <AO>
	 * @param access_obj 要移除访问安全检查的对象
	 * @return
	 */
	public static <AO extends AccessibleObject> AO removeAccessCheck(AO access_obj) {
		unsafe.putBoolean(access_obj, java_lang_reflect_AccessibleObject_override_OFFSET, true);
		return access_obj;
	}

	/**
	 * 使用反射无视权限访问成员，如果是静态成员则传入Class<?>，非静态成员则传入对象本身，jdk.internal.reflect.Reflection会对反射获取的字段进行过滤，因此这些字段不能访问。如需访问使用Handle的方法进行
	 * 
	 * @param obj        非静态成员所属对象本身或静态成员对应的Class<?>
	 * @param field_name 要访问的字段
	 * @return 成员的值
	 */
	public static Object access(Object obj, String field_name) {
		try {
			Field field = Manipulator.removeAccessCheck(Reflect.getField(obj, field_name));
			return field.get(Modifier.isStatic(field.getModifiers()) ? null : obj);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException ex) {
			System.err.println("access failed. obj=" + obj.toString() + ", field_name=" + field_name);
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用反射无视权限调用方法，如果是静态方法则传入Class<?>，非静态方法则传入对象本身。jdk.internal.reflect.Reflection会对反射获取的方法进行过滤，因此这些方法不能访问。如需访问使用Handle的方法进行
	 * 
	 * @param obj
	 * @param method_name
	 * @param arg_types
	 * @param args
	 */
	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		try {
			Method method = Manipulator.removeAccessCheck(Reflect.getMethod(obj, method_name, arg_types));
			return method.invoke(Modifier.isStatic(method.getModifiers()) ? null : obj, args);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException ex) {
			System.err.println("invoke failed. obj=" + obj.toString() + ", method_name=" + method_name);
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取jdk.internal.access.SharedSecrets中的访问对象
	 * 
	 * @param access_name 访问对象的类名，不包含包名
	 * @return 访问对象
	 */
	public static Object getAccess(String access_name) {
		return invoke(SharedSecrets, "get" + access_name, null);
	}

	/**
	 * 调用internalUnsafe的方法
	 * 
	 * @param method_name 方法名称
	 * @param arg_types   参数类型
	 * @param args        实参
	 * @return
	 */
	public static Object invokeInternalUnsafeMethod(String method_name, Class<?>[] arg_types, Object... args) {
		try {
			return invoke(internalUnsafe, method_name, arg_types, args);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 没有任何安全检查的Unsafe.objectFieldOffset方法，可以修改record class的成员
	 * 
	 * @param field
	 * @return
	 */
	public static long objectFieldOffset(Field field) {
		long offset = -1;
		try {
			offset = (long) objectFieldOffset.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return offset;
	}

	public static Object staticFieldBase(Field field) {
		try {
			return staticFieldBase.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long staticFieldOffset(Field field) {
		long offset = -1;
		try {
			offset = (long) staticFieldOffset.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return offset;
	}

	/**
	 * 获取指定类的常量池
	 * 
	 * @param clazz
	 * @return
	 */
	public static Object getConstantPool(Class<?> clazz) {
		try {
			return getConstantPool.invoke(JavaLangAccess, clazz);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 无视访问权限和修饰符修改Object值，如果是静态成员忽略obj参数.此方法对于HiddenClass和record同样有效
	 * 
	 * @param obj   要修改值的对象
	 * @param field 要修改的Field
	 * @param value 要修改的值
	 * @return
	 */
	public static boolean setObjectValue(Object obj, Field field, Object value) {
		if (field == null)
			return false;
		if (Modifier.isStatic(field.getModifiers()))
			unsafe.putObject(staticFieldBase(field), staticFieldOffset(field), value);
		else
			unsafe.putObject(obj, objectFieldOffset(field), value);
		return true;
	}

	public static boolean setObjectValue(Object obj, String field, Object value) {
		return setObjectValue(obj, Reflect.getField(obj, field), value);
	}

	public static boolean setLongValue(Object obj, Field field, long value) {
		if (field == null)
			return false;
		if (Modifier.isStatic(field.getModifiers()))
			unsafe.putLong(staticFieldBase(field), staticFieldOffset(field), value);
		else
			unsafe.putLong(obj, objectFieldOffset(field), value);
		return true;
	}

	public static boolean setLongValue(Object obj, String field, long value) {
		return setLongValue(obj, Reflect.getField(obj, field), value);
	}

	public static boolean setBooleanValue(Object obj, Field field, boolean value) {
		if (field == null)
			return false;
		if (Modifier.isStatic(field.getModifiers()))
			unsafe.putBoolean(staticFieldBase(field), staticFieldOffset(field), value);
		else
			unsafe.putBoolean(obj, objectFieldOffset(field), value);
		return true;
	}

	public static boolean setBooleanValue(Object obj, String field, boolean value) {
		return setBooleanValue(obj, Reflect.getField(obj, field), value);
	}
}
