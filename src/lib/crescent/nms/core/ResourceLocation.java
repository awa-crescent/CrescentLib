package lib.crescent.nms.core;

import lib.crescent.Manipulator;
import lib.crescent.Reflect;
import lib.crescent.nms.MappingsEntry;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ResourceLocation {
	protected String namespace;
	protected String location;// 又名id

	public ResourceLocation(String namespaced_id) {
		String[] namespace_id = parseNamespacedID(namespaced_id);
		this.namespace = namespace_id[0];
		this.location = namespace_id[1];
	}

	public ResourceLocation(String namespace, String id) {
		this.namespace = namespace;
		this.location = id;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return namespace + ':' + location;
	}

	/**
	 * 
	 * @param <T>               注册表类型
	 * @param resource_key      ResourceKey参数定义于net.minecraft.core.registries.Registries
	 * @param resource_location 带命名空间的id，或者说是ResourceLocation字符串
	 * @return 返回name对应的T类型注册表的ResourceKey
	 */
	public static <T> ResourceKey<T> getResourceKey(ResourceKey<? extends IRegistry<T>> resource_key, String resource_location) {
		return ResourceKey.create(resource_key, getResourceLocationFromNamespacedID(resource_location));
	}

	/**
	 * 
	 * @param <T>          注册表类型
	 * @param resource_key ResourceKey参数定义于net.minecraft.core.registries.Registries
	 * @param namespace    命名空间
	 * @param id           命名空间 ID
	 * @return 返回name对应的T类型注册表的ResourceKey
	 */
	public static <T> ResourceKey<T> getResourceKey(ResourceKey<? extends IRegistry<T>> resource_key, String namespace, String id) {
		return ResourceKey.create(resource_key, MinecraftKey.fromNamespaceAndPath(namespace, id));
	}

	/**
	 * 由于获取TagKey时要传入ResourceKey（注册类型），因此该函数用于从TagKey中获取私有属性ResourceKey registry
	 * 
	 * @param <T>
	 * @param tag_key 要获取ResourceKey的tag
	 * @return 返回tag_key的ResourceKey（注册类型）
	 */
	@SuppressWarnings("unchecked")
	public static <T> ResourceKey<? extends IRegistry<T>> getResourceKey(TagKey<T> tag_key) {
		return (ResourceKey<? extends IRegistry<T>>) Reflect.getValue(tag_key, "registry");
	}

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

	/**
	 * 通过带命名空间的id获取ResourceLocation，不检查命名空间和路径是否合法
	 * 
	 * @param namespaced_id
	 * @return
	 */
	public static MinecraftKey getResourceLocationFromNamespacedID(String namespaced_id) {
		String[] namespace_id = parseNamespacedID(namespaced_id);
		MinecraftKey resource_location = Manipulator.allocateInstance(MinecraftKey.class);
		Manipulator.setObjectValue(resource_location, MappingsEntry.getObfuscatedName("net.minecraft.resources.ResourceLocation.namespace"), namespace_id[0]);
		Manipulator.setObjectValue(resource_location, MappingsEntry.getObfuscatedName("net.minecraft.resources.ResourceLocation.path"), namespace_id[1]);
		return resource_location;
	}

	public MinecraftKey castToNMS() {
		MinecraftKey resource_location = Manipulator.allocateInstance(MinecraftKey.class);
		Manipulator.setObjectValue(resource_location, MappingsEntry.getObfuscatedName("net.minecraft.resources.ResourceLocation.namespace"), namespace);
		Manipulator.setObjectValue(resource_location, MappingsEntry.getObfuscatedName("net.minecraft.resources.ResourceLocation.path"), location);
		return resource_location;
	}
}
