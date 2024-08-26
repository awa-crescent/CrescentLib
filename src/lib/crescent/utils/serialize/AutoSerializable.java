package lib.crescent.utils.serialize;

import org.bukkit.plugin.Plugin;

public interface AutoSerializable {
	/**
	 * 对象被拉取时需要执行的操作
	 */
	default public void onPull() {

	}

	default public void onPush() {

	}

	/**
	 * 反序列化时要做的事情，不需要注册监听器，AutoSerialization会自动注册
	 * 
	 * @param ref_name 该对象（或对象的成员、成员的成员...）对应的引用名称
	 * @param plugin   反序列化该对象的插件
	 */
	default public void onDeserialize(String ref_name, Plugin plugin) {

	}
}
