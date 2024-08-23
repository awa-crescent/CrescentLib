package lib.crescent;

import java.lang.management.ManagementFactory;
import java.lang.management.PlatformManagedObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import com.sun.management.HotSpotDiagnosticMXBean;
//import sun.management.RuntimeImpl;
//import sun.management.VMManagementImpl;
//import jdk.internal.loader.BootLoader;

@SuppressWarnings({ "unchecked" })
public abstract class VMEntry {
	public static final int NATIVE_JVM_BIT_VERSION;// 64或32
	public static final boolean NATIVE_JVM_HOTSPOT;// JVM是否是HotSpot，如果是才能获取JVM参数进而判断指针是否压缩
	public static final boolean NATIVE_JVM_COMPRESSED_OOPS;
	// public static final ArrayList<URL> BOOTSTRAP_CLASSPATH = null;

	private static final Object VMManagement = null;
	private static final Properties Properties = null;

	private static final Class<ManagementFactory> ManagementFactoryClass = null;
	private static final HotSpotDiagnosticMXBean HotSpotDiagnosticMXBean = null;
	private static final Object RuntimeMXBean = null;

	private static final Class<?> ClassLoaders = null;

	private static final Method getVMOption = null;
	private static final Method VMOption_getValue = null;

	static {
		String bit_version = System.getProperty("sun.arch.data.model");
		if (bit_version != null && bit_version.contains("64"))
			NATIVE_JVM_BIT_VERSION = 64;
		else {
			String arch = System.getProperty("os.arch");
			if (arch != null && arch.contains("64"))
				NATIVE_JVM_BIT_VERSION = 64;
			else
				NATIVE_JVM_BIT_VERSION = 32;
		}
		boolean hotspot = false;
		boolean compressed_oops = false;
		try {
			Manipulator.setObjectValue(VMEntry.class, "ManagementFactoryClass", Class.forName("java.lang.management.ManagementFactory"));
			Class<HotSpotDiagnosticMXBean> HotSpotDiagnosticMXBeanClass = (Class<com.sun.management.HotSpotDiagnosticMXBean>) Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
			Manipulator.setObjectValue(VMEntry.class, "HotSpotDiagnosticMXBean", invokeManagementFactory("getPlatformMXBean", HotSpotDiagnosticMXBeanClass));
			Manipulator.setObjectValue(VMEntry.class, "RuntimeMXBean", invokeManagementFactory("getRuntimeMXBean"));
			Manipulator.setObjectValue(VMEntry.class, "VMManagement", Manipulator.access(RuntimeMXBean, "jvm"));
			Manipulator.setObjectValue(VMEntry.class, "Properties", Manipulator.access(System.class, "props"));
			if (HotSpotDiagnosticMXBean != null) {
				hotspot = true;
				Manipulator.setObjectValue(VMEntry.class, "getVMOption", Reflect.getMethod(HotSpotDiagnosticMXBeanClass, "getVMOption", String.class));
				if (NATIVE_JVM_BIT_VERSION == 64) {// 64位JVM需要检查是否启用了指针压缩
					Object oops_option = getVMOption.invoke(HotSpotDiagnosticMXBean, "UseCompressedOops");
					Manipulator.setObjectValue(VMEntry.class, "VMOption_getValue", Reflect.getMethod(oops_option, "getValue"));
					compressed_oops = Boolean.parseBoolean(VMOption_getValue.invoke(oops_option).toString());
				} else
					compressed_oops = false;
			}
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | SecurityException | IllegalArgumentException ex) {
			hotspot = false;
		}
		try {
			Manipulator.setObjectValue(VMEntry.class, "ClassLoaders", Class.forName("jdk.internal.loader.ClassLoaders"));
			Manipulator.setObjectValue(VMEntry.class, "BOOTSTRAP_CLASSPATH", getBuiltinClassLoaderClassPath("BOOT_LOADER"));
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		NATIVE_JVM_HOTSPOT = hotspot;
		NATIVE_JVM_COMPRESSED_OOPS = compressed_oops;
	}

	public static Object invokeManagementFactory(String method_name, Class<?>[] arg_types, Object args) {
		try {
			return ManagementFactoryClass.getDeclaredMethod(method_name, arg_types).invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object invokeManagementFactory(String method_name) {
		try {
			return ManagementFactoryClass.getDeclaredMethod(method_name).invoke(null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <T extends PlatformManagedObject> T invokeManagementFactory(String method_name, Class<T> mxbean_interface) {
		try {
			return (T) ManagementFactoryClass.getDeclaredMethod(method_name, Class.class).invoke(null, mxbean_interface);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object invokeVMManagement(String method_name, Class<?>[] arg_types, Object args) {
		try {
			return Manipulator.invoke(VMManagement, method_name, arg_types, args);
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object accessBuiltinClassLoaders(String class_loader_name) {
		try {
			return Manipulator.access(ClassLoaders, class_loader_name);
		} catch (SecurityException | IllegalArgumentException ex) {
			System.err.println("Class loader name can only be BOOT_LOADER | PLATFORM_LOADER | APP_LOADER");
		}
		return null;
	}

	public static ArrayList<URL> getBuiltinClassLoaderClassPath(String class_loader_name) {
		try {
			Object class_loader = accessBuiltinClassLoaders(class_loader_name);
			Object url_classpath = Manipulator.access(class_loader, "ucp");// jdk.internal.loader.URLClassPath
			if (url_classpath != null)// BOOT_LOADER的ucp为null
				return (ArrayList<URL>) Manipulator.access(url_classpath, "path");
		} catch (SecurityException | IllegalArgumentException ex) {
			System.err.println("Cannot get class loader classpath");
		}
		return null;
	}

	public static String getSystemProperty(String key) {
		return Properties.getProperty(key);
	}

	public static void setSystemProperty(String key, String value) {
		Properties.setProperty(key, value);
	}

	/**
	 * 获取指定的boolean类型的VM参数
	 * 
	 * @param option_name 参数名称，例如UseCompressedOops
	 * @return
	 */
	public static boolean getBooleanOption(String option_name) {
		try {
			return Boolean.parseBoolean(VMOption_getValue.invoke(getVMOption.invoke(HotSpotDiagnosticMXBean, option_name)).toString());
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
