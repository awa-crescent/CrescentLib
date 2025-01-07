package lib.crescent.nms;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import lib.crescent.Manipulator;

public class NMSManipulator {
	public static Object access(Object obj, String field_name) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field_name);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field_name);
		}
		return Manipulator.access(obj, obfuscated_name);
	}

	public static boolean setObject(Object obj, String field, Object value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setObject(obj, obfuscated_name, value);
	}

	public static Object getObject(Object obj, String field) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.getObject(obj, obfuscated_name);
	}

	public static boolean setLong(Object obj, String field, long value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setLong(obj, obfuscated_name, value);
	}

	public static boolean setBoolean(Object obj, String field, boolean value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setBoolean(obj, obfuscated_name, value);
	}

	public static boolean setInt(Object obj, String field, int value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setInt(obj, obfuscated_name, value);
	}

	public static boolean setDouble(Object obj, String field, double value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setDouble(obj, obfuscated_name, value);
	}

	public static boolean setFloat(Object obj, String field, float value) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(field);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated field name of " + field);
		}
		return Manipulator.setFloat(obj, obfuscated_name, value);
	}

	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		String obfuscated_name = MappingsEntry.getObfuscatedName(method_name);
		if (obfuscated_name == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot get obfuscated method name of " + method_name);
		}
		return Manipulator.invoke(obj, obfuscated_name, arg_types, args);
	}
}
