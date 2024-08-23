package lib.crescent.enchantment;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import lib.crescent.utils.trigger.Trigger;
import lib.crescent.utils.trigger.TriggerCondition;

public abstract class TriggerableEnchantment extends EnchantmentEntry {
	protected LivingEntity trigger_entity = null;// 判断能否触发附魔效果的实体，将检测它的对应位置的物品是否有该附魔
	protected ItemStack trigger_item = null;// 判断能否触发附魔效果的物品，由trigger_entity持有
	private Trigger trigger;
	private double enchantment_value;
	private double[] enchantment_values;

	public TriggerableEnchantment(String plugin_name, String namespace, String enchantment_id, String locale_key) {
		super(plugin_name, namespace, enchantment_id, locale_key);
		trigger = new Trigger();
	}

	/**
	 * 设置触发实体。在Listener监听的事件中需要使用该方法设置触发附魔效果的实体
	 * 
	 * @param trigger_entity
	 */
	public final void setTriggerEntity(LivingEntity trigger_entity) {
		this.trigger_entity = trigger_entity;
		EntityEquipment trigger_entitys_equipment = trigger_entity.getEquipment();
		if (trigger_entitys_equipment != null)
			setTriggerItem(trigger_entitys_equipment);
		else
			trigger_item = null;
		resolveTriggerChanceAndEnchantmentValue();
	}

	/**
	 * 设置玩家物品栏索引为idx的物品为触发物品。如果实体不是玩家则忽略。用于一些特殊情况，例如PlayerItemHeldEvent事件中玩家切换主手武器时触发
	 * 
	 * @param idx 触发物品在物品栏的索引
	 */
	public final void setTriggerItemAtIndex(int idx) {
		if (trigger_entity instanceof Player player)
			trigger_item = player.getInventory().getItem(idx);
		resolveTriggerChanceAndEnchantmentValue();
	}

	protected final void resolveTriggerChanceAndEnchantmentValue() {
		int level = getLevel();
		trigger.setTriggerChance(getTriggerChance(level)).setTriggerChances(getTriggerChances(level));
		enchantment_value = getEnchantmentValue(level);
		enchantment_values = getEnchantmentValues(level);
	}

	/**
	 * 设置触发物品
	 * 
	 * @param trigger_entitys_equipment 触发附魔效果实体的装备
	 */
	protected abstract void setTriggerItem(EntityEquipment trigger_entitys_equipment);

	/**
	 * 如果需要不同魔咒等级有不同触发效果的概率，则重写该方法。默认触发概率是100%
	 * 
	 * @param level 附魔等级。对于盔甲而言它可以是四件的该附魔等级总和
	 * @return level等级的附魔对应的触发概率
	 */
	public double getTriggerChance(int level) {
		return 1;
	}

	/**
	 * 如果需要不同魔咒等级有多个概率判断，则重写该方法
	 * 
	 * @param level 附魔等级。对于盔甲而言它可以是四件的该附魔等级总和
	 * @return level等级的附魔对应的触发概率
	 */
	public double[] getTriggerChances(int level) {
		return null;
	}

	@SafeVarargs
	protected final <T> void setExtraTriggerCondition(TriggerCondition<T>... extra_conditions) {
		this.trigger.setExtraTriggerCondition(extra_conditions);
	}

	/**
	 * 如果需要不同魔咒等级有不同数值（用于实现效果），且一个等级只对应一个数值，则重写该方法
	 * 
	 * @param level 附魔等级。对于盔甲而言它可以是四件的该附魔等级总和
	 * @return level等级的附魔对应的数值
	 */
	public double getEnchantmentValue(int level) {
		return 0;
	}

	/**
	 * 如果需要不同魔咒等级有不同数值（用于实现效果），且数值数量大于1，则可以不重写getEnchantmentValue()，而重写该方法
	 * 
	 * @param level 附魔等级。对于盔甲而言它可以是四件的该附魔等级总和
	 * @return level等级的附魔对应的数值
	 */
	public double[] getEnchantmentValues(int level) {
		return null;
	}

	/**
	 * 获取附魔触发物品的附魔等级，如果trigger_item==null则需要重写该方法。例如盔甲附魔就需要计算4个armor slots的附魔等级总和
	 * 
	 * @return 返回触发物品的附魔等级
	 */
	public int getLevel() {
		return getLevel(trigger_item);
	}

	/**
	 * 检查触发实体所持物品能否触发附魔物品，对于需要检测的物品数量大于1时，可无视trigger_item自己设置触发条件
	 * 
	 * @param trigger_item 触发物品
	 * @return 是否触发附魔效果
	 */
	protected boolean checkItemTriggerable(ItemStack trigger_item) {
		return this.containsEnchantment(trigger_item);
	}

	/**
	 * 判断是否能触发附魔效果。要求该魔咒等级（或总和）>0，且getTriggerChance()规定的概率在判定中得到该魔咒可以触发，不应用额外条件
	 * 
	 * @return 是否触发附魔效果
	 */
	protected final boolean resolveTrigger() {
		return checkItemTriggerable(trigger_item) && trigger.resolveTrigger();
	}

	/**
	 * 判断是否能触发附魔效果。要求该魔咒等级（或总和）>0，且getTriggerChance()规定的概率在判定中得到该魔咒可以触发，应用setExtraTriggerCondition(T
	 * related_arg)设置的额外条件
	 * 
	 * @return 是否触发附魔效果
	 */
	protected <T> boolean resolveTrigger(T related_arg, int... condition_indexes) {
		return resolveTrigger() && trigger.resolveExtraTriggerCondition(related_arg, condition_indexes);// 优先调用子类的resolveTrigger()
	}

	/**
	 * 判断是否能触发附魔效果。要求该魔咒等级（或总和）>0，且getTriggerChances()规定的概率数组，其中索引indexes指定的概率均在判定中得到true，则该魔咒可以触发，不应用额外条件
	 * 
	 * @return 是否触发附魔效果
	 */
	protected final boolean resolveTrigger(int... indexes) {
		return checkItemTriggerable(trigger_item) && trigger.resolveTrigger(indexes);
	}

	/**
	 * 判断是否能触发附魔效果。要求该魔咒等级（或总和）>0，且getTriggerChances()规定的概率数组，其中索引indexes指定的概率均在判定中得到true，则该魔咒可以触发，应用setExtraTriggerCondition()设置的额外条件
	 * 
	 * @return 是否触发附魔效果
	 */
	protected <T> boolean resolveTrigger(T related_arg) {
		return checkItemTriggerable(trigger_item) && trigger.resolveTrigger(related_arg);
	}

	/**
	 * 根据附魔等级得到最终的附魔效果值，要先使用getEnchantmentValue()规定各个可能的附魔等级对应的效果值
	 * 
	 * @return
	 */
	protected final double resolveEnchantmentValue() {
		return enchantment_value;
	}

	/**
	 * 根据附魔等级得到最终的附魔效果值，要先使用getEnchantmentValues()规定各个可能的附魔等级对应的效果值
	 * 
	 * @param index 数组索引，返回getEnchantmentValues()中的索引为index的元素
	 * @return 数组指定位置的值
	 */
	protected final double resolveEnchantmentValue(int index) {
		return enchantment_values[index];
	}

	/**
	 * 根据附魔等级得到最终的getEnchantmentValues()规定的数值数组
	 * 
	 * @return 数值数组
	 */
	protected final double[] resolveEnchantmentValues() {
		return enchantment_values;
	}
}
