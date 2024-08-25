package lib.crescent.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import lib.crescent.utils.serialize.AutoSerializable;

/**
 * 能记录对目标对象的属性值变更，用于追踪对目标的属性值更改了多少，例如可以用于死亡时复原实体的属性值
 */
public class EntityAttributeModifier implements Listener, Serializable, AutoSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7221589165257970392L;

	@Override
	public String toString() {
		return "[Enternal attributes modified values=" + enternal_modified_entities.toString() + ", Clear-on-death attributes modified values=" + clear_on_death_modified_entities.toString() + "]";
	}

	// 某一个Attribute记录的所有实体以及对应修改值，只记录修改值，不实际对实体进行属性修改操作
	protected class AttributeModifierEntityEntry implements Externalizable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3529652973877636835L;

		private Attribute attribute;
		private HashMap<UUID, Double> primary_values;
		private HashMap<UUID, Double> modifiers;

		public AttributeModifierEntityEntry(String attribute) {
			this.attribute = Attribute.valueOf(attribute);
			primary_values = new HashMap<>();
			modifiers = new HashMap<>();
		}

		@Override
		public String toString() {
			return "[Attribute=" + attribute.getKey().getKey() + ", Primary values=" + primary_values.toString() + ", Modified values=" + modifiers.toString() + ']';
		}

		public Attribute getAttribute() {
			return attribute;
		}

		public boolean isEmpty() {
			return modifiers.isEmpty();
		}

		/**
		 * 获取该属性的修饰值，如果没有修饰则返回0
		 * 
		 * @param uuid 要获取该属性的修改值的实体UUID
		 * @return
		 */
		public double getModifiedBaseValue(UUID uuid) {
			Double modified_value = modifiers.get(uuid);
			return modified_value == null ? 0 : modified_value;
		}

		/**
		 * 判断指定实体是否修改过该属性值
		 * 
		 * @param uuid 要获取该属性的修改值的实体UUID
		 * @return 是否修饰过
		 */
		public boolean hasModified(UUID uuid) {
			Double modified_value = modifiers.get(uuid);
			return modified_value == null || modified_value == 0;
		}

		public AttributeModifierEntityEntry setPrimaryValue(UUID uuid, double primary) {
			primary_values.put(uuid, primary);
			return this;
		}

		public AttributeModifierEntityEntry setPrimaryValue(LivingEntity entity) {
			return setPrimaryValue(entity.getUniqueId(), entity.getAttribute(attribute).getBaseValue());
		}

		public double getPrimaryBaseValue(UUID uuid) {
			Double primary = primary_values.get(uuid);
			return primary == null ? 0 : primary;
		}

		public AttributeModifierEntityEntry addModifiedBaseValue(UUID uuid, double value) {
			Double modified_value = modifiers.get(uuid);
			if (modified_value == null) {// 没有记录则新建条目
				modifiers.put(uuid, value);
			} else {
				modifiers.put(uuid, modified_value + value);
			}
			return this;
		}

		public AttributeModifierEntityEntry subModifiedBaseValue(UUID uuid, double value) {
			return addModifiedBaseValue(uuid, -value);
		}

		public AttributeModifierEntityEntry addModifiedBaseValue(LivingEntity entity, double value) {
			UUID uuid = entity.getUniqueId();
			Double modified_value = modifiers.get(uuid);
			if (modified_value == null) {// 没有记录则新建条目
				modifiers.put(uuid, value);
				setPrimaryValue(entity);
			} else {
				modifiers.put(uuid, modified_value + value);
			}
			return this;
		}

		public AttributeModifierEntityEntry subModifiedBaseValue(LivingEntity entity, double value) {
			return addModifiedBaseValue(entity, -value);
		}

		public double resetModifiedBaseValue(UUID uuid) {
			Double value = modifiers.remove(uuid);
			return value == null ? 0 : value;
		}

		public double resetModifiedBaseValue(LivingEntity entity) {
			return resetModifiedBaseValue(entity.getUniqueId());
		}

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeObject(attribute.getKey().getKey());// 属性都是minecraft命名空间
			out.writeObject(primary_values);
			out.writeObject(modifiers);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
			attribute = Attribute.valueOf((String) in.readObject());
			primary_values = (HashMap<UUID, Double>) in.readObject();
			modifiers = (HashMap<UUID, Double>) in.readObject();
		}
	}

	// 属性增加记录为正，减少记录为负。死亡后清除的会单独记录以减少遍历次数，提高性能
	private HashMap<String, AttributeModifierEntityEntry> enternal_modified_entities;// 永久改变的值，死亡后保持（只有玩家能死亡后复活）
	private HashMap<String, AttributeModifierEntityEntry> clear_on_death_modified_entities;// 死亡后将清除的值

	/**
	 * 构建一个能记录属性更改的对象，并且可以设置死亡后复原修改的属性（不会和别的EntityAttributeModifier对象行为冲突）。第一次修改某个属性时会记录修改前的原始值
	 * 
	 * @param record_primary 是否要
	 */
	public EntityAttributeModifier(String plugin_name) {
		enternal_modified_entities = new HashMap<>();
		clear_on_death_modified_entities = new HashMap<>();
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(plugin_name);
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);// 注册该类的事件监听，注册后方可使用@EventHandler
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {// 实体死亡后清除属性
		resetBaseValue(event.getEntity(), ValueType.CLEAR_ON_DEATH);
	}

	/**
	 * 获取修饰属性变更的值，属性增加记录为正，减少记录为负
	 * 
	 * @param entity 记录的实体
	 * @param attrib 记录的属性
	 * @param type   选择获取永久修饰、死亡清除修饰或者两者之和
	 * @return 该对象记录的实体修饰值
	 */
	public double getModifiedBaseValue(LivingEntity entity, String attrib, ValueType type) {
		UUID uuid = entity.getUniqueId();
		AttributeModifierEntityEntry enternal_attribs = null;
		AttributeModifierEntityEntry clear_on_death_attribs = null;
		switch (type) {
		case ENTERNAL:
			if ((enternal_attribs = enternal_modified_entities.get(attrib)) == null)// 没有实体曾经修饰该属性则直接返回0
				return 0;
			return enternal_attribs.getModifiedBaseValue(uuid);// 获取该实体的修饰值，如果该实体没有修饰这个值就是0
		case CLEAR_ON_DEATH:
			if ((clear_on_death_attribs = clear_on_death_modified_entities.get(attrib)) == null)
				return 0;
			return clear_on_death_attribs.getModifiedBaseValue(uuid);
		case ALL:
			double result_value = 0;
			if ((enternal_attribs = enternal_modified_entities.get(attrib)) != null) {
				result_value += enternal_attribs.getModifiedBaseValue(uuid);
			}
			if ((clear_on_death_attribs = clear_on_death_modified_entities.get(attrib)) != null) {
				result_value += enternal_attribs.getModifiedBaseValue(uuid);
			}
			return result_value;
		}
		return 0;
	}

	/**
	 * 增加实体属性值
	 * 
	 * @param entity         要增加属性值的实体
	 * @param attrib         要增加的属性
	 * @param value          要增加的值
	 * @param clear_on_death 是否在死亡后撤销该值的更改
	 * @return
	 */
	public EntityAttributeModifier addBaseValue(LivingEntity entity, String attrib, double value, boolean clear_on_death) {
		AttributeModifierEntityEntry modified_attribs = null;
		AttributeInstance attrib_ins = entity.getAttribute(Attribute.valueOf(attrib));
		if (clear_on_death) {
			modified_attribs = clear_on_death_modified_entities.get(attrib);
			if (modified_attribs == null) {
				modified_attribs = new AttributeModifierEntityEntry(attrib);
				clear_on_death_modified_entities.put(attrib, modified_attribs);
			}
		} else {
			modified_attribs = enternal_modified_entities.get(attrib);
			if (modified_attribs == null) {// 没有修饰属性则新建HashMap储存属性修改值
				modified_attribs = new AttributeModifierEntityEntry(attrib);
				enternal_modified_entities.put(attrib, modified_attribs);
			}
		}
		modified_attribs.addModifiedBaseValue(entity, value);
		attrib_ins.setBaseValue(attrib_ins.getBaseValue() + value);
		return this;
	}

	public EntityAttributeModifier subBaseValue(LivingEntity entity, String attrib, double value, boolean clear_on_death) {
		return addBaseValue(entity, attrib, -value, clear_on_death);
	}

	/**
	 * 在记录用的HashMap中清除修改的属性值，并且立即将更改应用到实体上
	 * 
	 * @param entity 要清除属性的实体
	 * @param attrib 要清除的属性
	 * @param type   要清除的类型
	 * @return
	 */
	public EntityAttributeModifier resetBaseValue(LivingEntity entity, String attrib, ValueType type) {
		UUID uuid = entity.getUniqueId();
		AttributeInstance present_attrib_instance = entity.getAttribute(Attribute.valueOf(attrib));
		AttributeModifierEntityEntry enternal_attribs = null;
		AttributeModifierEntityEntry clear_on_death_attribs = null;
		boolean reset_enternal = false;
		boolean reset_clear_on_death = false;
		switch (type) {
		case ENTERNAL:
			if ((enternal_attribs = enternal_modified_entities.get(attrib)) == null)// 没有实体修饰该属性则直接返回
				return this;
			reset_enternal = true;
			break;
		case CLEAR_ON_DEATH:
			if ((clear_on_death_attribs = clear_on_death_modified_entities.get(attrib)) == null)// 没有修饰该实体直接返回
				return this;
			reset_clear_on_death = true;
			break;
		case ALL:
			reset_enternal = true;
			reset_clear_on_death = true;
			break;
		default:
			break;
		}
		if (reset_enternal) {
			present_attrib_instance.setBaseValue(present_attrib_instance.getBaseValue() - enternal_attribs.resetModifiedBaseValue(uuid));
			if (enternal_attribs.isEmpty())
				enternal_modified_entities.remove(attrib);
		}
		if (reset_clear_on_death) {
			present_attrib_instance.setBaseValue(present_attrib_instance.getBaseValue() - clear_on_death_attribs.resetModifiedBaseValue(uuid));
			if (clear_on_death_attribs.isEmpty())
				clear_on_death_modified_entities.remove(attrib);
		}
		return this;
	}

	/**
	 * 在记录用的HashMap中清除所有修改的属性值，并且立即将更改应用到实体上
	 * 
	 * @param entity 要清除属性的实体
	 * @param type   要清除的类型
	 * @return
	 */
	public EntityAttributeModifier resetBaseValue(LivingEntity entity, ValueType type) {
		UUID uuid = entity.getUniqueId();
		Set<Entry<String, AttributeModifierEntityEntry>> enternal_attribs_entry_set = enternal_modified_entities.entrySet();
		Set<Entry<String, AttributeModifierEntityEntry>> clear_on_death_attribs_entry_set = clear_on_death_modified_entities.entrySet();
		boolean reset_enternal = false;
		boolean reset_clear_on_death = false;
		switch (type) {
		case ENTERNAL:
			reset_enternal = true;
			break;
		case CLEAR_ON_DEATH:
			reset_clear_on_death = true;
			break;
		case ALL:
			reset_enternal = true;
			reset_clear_on_death = true;
			break;
		default:
			break;
		}
		if (reset_enternal)
			for (Map.Entry<String, AttributeModifierEntityEntry> entity_entry : enternal_attribs_entry_set) {
				AttributeModifierEntityEntry enternal_attribs = entity_entry.getValue();
				AttributeInstance present_attrib_instance = entity.getAttribute(enternal_attribs.getAttribute());
				present_attrib_instance.setBaseValue(present_attrib_instance.getBaseValue() - enternal_attribs.resetModifiedBaseValue(uuid));
				if (enternal_attribs.isEmpty())
					enternal_modified_entities.remove(enternal_attribs.getAttribute().getKey().getKey());
			}
		if (reset_clear_on_death)
			for (Map.Entry<String, AttributeModifierEntityEntry> entity_entry : clear_on_death_attribs_entry_set) {
				AttributeModifierEntityEntry clear_on_death_attribs = entity_entry.getValue();
				AttributeInstance present_attrib_instance = entity.getAttribute(clear_on_death_attribs.getAttribute());
				present_attrib_instance.setBaseValue(present_attrib_instance.getBaseValue() - clear_on_death_attribs.resetModifiedBaseValue(uuid));
				if (clear_on_death_attribs.isEmpty())
					clear_on_death_modified_entities.remove(clear_on_death_attribs.getAttribute().getKey().getKey());
			}
		return this;
	}

	/**
	 * 获取应用修改前的实体的基础属性值
	 * 
	 * @param entity 要获取原本基础属性值的实体
	 * @param attrib 要获取的属性
	 * @param type   获取的类型
	 * @return 修改前的属性基础数值
	 */
	public double getPrimaryBaseValue(LivingEntity entity, String attrib, ValueType type) {
		UUID uuid = entity.getUniqueId();
		AttributeModifierEntityEntry modified_attrib_entry = null;
		switch (type) {
		case CLEAR_ON_DEATH:
			if ((modified_attrib_entry = clear_on_death_modified_entities.get(attrib)) == null)
				return -1;
			break;
		case ENTERNAL:
			if ((modified_attrib_entry = enternal_modified_entities.get(attrib)) == null)
				return -1;
			break;
		default:
			return -1;
		}
		return modified_attrib_entry.getPrimaryBaseValue(uuid);
	}
}
