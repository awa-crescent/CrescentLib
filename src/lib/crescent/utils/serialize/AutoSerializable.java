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
	 */
	default public void onDeserialize(Plugin plugin) {

	}
}
