package lib.crescent.entity;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityRelatedObject<T> implements Listener {
	protected HashMap<UUID, T> enternal_related_object;
	protected HashMap<UUID, T> clear_on_death_related_object;

	public EntityRelatedObject(String plugin_name) {
		enternal_related_object = new HashMap<>();
		clear_on_death_related_object = new HashMap<>();
		Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getServer().getPluginManager().getPlugin(plugin_name));
	}

	public EntityRelatedObject<T> setValue(LivingEntity entity, T value, boolean is_clear_on_death) {
		UUID uuid = entity.getUniqueId();
		if (is_clear_on_death)
			clear_on_death_related_object.put(uuid, value);
		else
			enternal_related_object.put(uuid, value);
		return this;
	}

	/**
	 * 获取计数。通常最好选择CLEAR_ON_DEATH或者ENTERNAL，而不要使用ALL。ALL返回null
	 * 
	 * @param entity 要获取计数器值的实体
	 * @param type   要获取的数值类型
	 * @return 返回数值对象
	 */
	public T getValue(LivingEntity entity, ValueType type) {
		UUID uuid = entity.getUniqueId();
		switch (type) {
		case CLEAR_ON_DEATH:
			return clear_on_death_related_object.get(uuid);
		case ENTERNAL:
			return enternal_related_object.get(uuid);
		case ALL:
			return null;
		}
		return null;
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {// 实体死亡后清除属性
		clear_on_death_related_object.remove(event.getEntity().getUniqueId());
	}
}
