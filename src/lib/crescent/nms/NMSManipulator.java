package lib.crescent.nms;

import lib.crescent.Manipulator;

public class NMSManipulator {
	public static Object access(Object obj, String field_name) {
		return Manipulator.access(obj, MappingsEntry.getObfuscatedName(field_name));
	}

	public static boolean setObjectValue(Object obj, String field, Object value) {
		return Manipulator.setObjectValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static Object getObjectValue(Object obj, String field) {
		return Manipulator.getObjectValue(obj, MappingsEntry.getObfuscatedName(field));
	}

	public static boolean setLongValue(Object obj, String field, long value) {
		return Manipulator.setLongValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setBooleanValue(Object obj, String field, boolean value) {
		return Manipulator.setBooleanValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setIntValue(Object obj, String field, int value) {
		return Manipulator.setIntValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setDoubleValue(Object obj, String field, double value) {
		return Manipulator.setDoubleValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static boolean setFloatValue(Object obj, String field, float value) {
		return Manipulator.setFloatValue(obj, MappingsEntry.getObfuscatedName(field), value);
	}

	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		return Manipulator.invoke(obj, MappingsEntry.getObfuscatedName(method_name), arg_types, args);
	}
}
