package lib.crescent.tag;

import net.minecraft.resources.MinecraftKey;

public class NamespacedKeyUtils {
	/**
	 * 解析带命名空间的id，返回数组[0]为命名空间（没有则是默认的minecraft）[1]为id
	 * 
	 * @param namespaced_id 带命名空间的id
	 * @return 命名空间和id
	 */
	public static String[] parseNamespacedID(String namespaced_id) {
		int delim_idx = namespaced_id.indexOf(':');
		String result[] = new String[] { "minecraft", null };
		if (delim_idx != -1)// 如果没有命名空间，则默认为minecraft空间
			result[0] = namespaced_id.substring(0, delim_idx);
		result[1] = namespaced_id.substring(delim_idx + 1);
		return result;
	}

	public static MinecraftKey getResourceLocationFromNamespacedID(String namespaced_id) {
		String[] namespace_id = NamespacedKeyUtils.parseNamespacedID(namespaced_id);
		return MinecraftKey.fromNamespaceAndPath(namespace_id[0], namespace_id[1]);
	}
}
