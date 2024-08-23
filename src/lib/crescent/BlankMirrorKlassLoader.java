package lib.crescent;

import lib.crescent.utils.reflex.JarUtils;

public class BlankMirrorKlassLoader {

	public static byte[] loadByteCode(Object target) {
		Class<?> target_clazz = target.getClass();
		byte[] bytecode = JarUtils.getJarResourceAsBytes(target_clazz, target_clazz.getName().replace('.', '/') + ".class");
		return bytecode;
	}
}
