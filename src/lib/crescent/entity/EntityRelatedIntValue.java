package lib.crescent.entity;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;

public class EntityRelatedIntValue extends EntityRelatedObject<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1195055807917831791L;

	public EntityRelatedIntValue(String plugin_name) {
		super(plugin_name);
	}

	/**
	 * 增加记录的值
	 * 
	 * @param <V>               必须是数字类型
	 * @param entity            要记录的实体
	 * @param value             要增加的值
	 * @param is_clear_on_death 是否是死亡清除值
	 * @return
	 */
	public <V extends Number> EntityRelatedIntValue increaseValue(LivingEntity entity, V value, boolean is_clear_on_death) {
		UUID uuid = entity.getUniqueId();
		if (is_clear_on_death) {
			Integer clear_on_death_value = clear_on_death_related_object.get(uuid);
			if (clear_on_death_value == null)
				clear_on_death_related_object.put(uuid, value.intValue());
			else
				clear_on_death_related_object.put(uuid, clear_on_death_value + value.intValue());
		} else {
			Integer enternal_value = enternal_related_object.get(uuid);
			if (enternal_value == null)
				enternal_related_object.put(uuid, value.intValue());
			else
				enternal_related_object.put(uuid, enternal_value + value.intValue());
		}
		return this;
	}

	public <V extends Number> EntityRelatedIntValue increaseValue(LivingEntity entity, boolean is_clear_on_death) {
		return increaseValue(entity, 1, is_clear_on_death);
	}

	/**
	 * 减少记录的值
	 * 
	 * @param <V>
	 * @param entity            要记录的实体
	 * @param value             要减少的值
	 * @param is_clear_on_death 是否是死亡清除值
	 * @return
	 */
	public <V extends Number> EntityRelatedIntValue decreaseValue(LivingEntity entity, V value, boolean is_clear_on_death) {
		UUID uuid = entity.getUniqueId();
		if (is_clear_on_death) {
			Integer clear_on_death_value = clear_on_death_related_object.get(uuid);
			if (clear_on_death_value == null)
				clear_on_death_related_object.put(uuid, -value.intValue());
			else
				clear_on_death_related_object.put(uuid, clear_on_death_value - value.intValue());
		} else {
			Integer enternal_value = enternal_related_object.get(uuid);
			if (enternal_value == null)
				enternal_related_object.put(uuid, -value.intValue());
			else
				enternal_related_object.put(uuid, enternal_value - value.intValue());
		}
		return this;
	}

	public <V extends Number> EntityRelatedIntValue decreaseValue(LivingEntity entity, boolean is_clear_on_death) {
		return decreaseValue(entity, 1, is_clear_on_death);
	}

	/**
	 * 获取计数。ALL返回两者之和
	 * 
	 * @param entity 要获取计数器值的实体
	 * @param type   要获取的数值类型
	 * @return 返回数值对象
	 */
	@Override
	public Integer getValue(LivingEntity entity, ValueType type) {
		UUID uuid = entity.getUniqueId();
		Integer cod_value = null;
		Integer e_value = null;
		switch (type) {
		case CLEAR_ON_DEATH:
			cod_value = clear_on_death_related_object.get(uuid);
			return cod_value == null ? 0 : cod_value;
		case ENTERNAL:
			e_value = enternal_related_object.get(uuid);
			return e_value == null ? 0 : e_value;
		case ALL:
			cod_value = clear_on_death_related_object.get(uuid);
			e_value = enternal_related_object.get(uuid);
			return (cod_value == null ? 0 : cod_value) + (e_value == null ? 0 : e_value);
		}
		return 0;
	}
}
