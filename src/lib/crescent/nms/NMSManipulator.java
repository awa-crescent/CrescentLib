package lib.crescent.nms;

import lib.crescent.Manipulator;

public class NMSManipulator {
	public static Object access(Object obj, String field_name) {
		return Manipulator.access(obj, MappingsEntry.getObfuscatedName(field_name));
	}

	public static boolean setObject(Object obj, String field, Object value) {
		return Manipulator.setObject(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static Object getObject(Object obj, String field) {
		return Manipulator.getObject(obj, MappingsEntry.getObfuscatedName(field));
	}

	public static boolean setLong(Object obj, String field, long value) {
		return Manipulator.setLong(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setBoolean(Object obj, String field, boolean value) {
		return Manipulator.setBoolean(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setInt(Object obj, String field, int value) {
		return Manipulator.setInt(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setDouble(Object obj, String field, double value) {
		return Manipulator.setDouble(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setFloat(Object obj, String field, float value) {
		return Manipulator.setFloat(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		return Manipulator.invoke(obj, MappingsEntry.getObfuscatedName(method_name), arg_types, args);
	}
}
