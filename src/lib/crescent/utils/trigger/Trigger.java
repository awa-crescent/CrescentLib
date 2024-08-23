package lib.crescent.utils.trigger;

import lib.crescent.utils.math.Random;

public class Trigger {
	private double trigger_chance;
	private double[] trigger_chances;
	private TriggerCondition<?>[] extra_trigger_conditions = null;

	/**
	 * 设置单个触发概率
	 * 
	 * @param trigger_chance 触发概率
	 * @return
	 */
	public final Trigger setTriggerChance(double trigger_chance) {
		this.trigger_chance = trigger_chance;
		return this;
	}

	/**
	 * 获取单个触发概率
	 * 
	 * @return 触发概率
	 */
	public final double getTriggerChance() {
		return trigger_chance;
	}

	/**
	 * 设置多个触发概率
	 * 
	 * @param trigger_chance 触发概率数组
	 * @return
	 */
	public final Trigger setTriggerChances(double[] trigger_chances) {
		this.trigger_chances = trigger_chances;
		return this;
	}

	/**
	 * 获取多个触发概率
	 * 
	 * @return 触发概率数组
	 */
	public final double[] getTriggerChances() {
		return trigger_chances;
	}

	/**
	 * 获取触发概率数组中的某个索引位置的概率
	 * 
	 * @param trigger_chance 概率索引组
	 * @return 索引对应的触发概率
	 */
	public final double getTriggerChance(int idx) {
		return trigger_chances[idx];
	}

	/**
	 * 设置基于的事件的额外触发条件
	 * 
	 * @param extra_conditions 额外触发条件
	 */
	public final Trigger setExtraTriggerCondition(TriggerCondition<?>... extra_conditions) {
		this.extra_trigger_conditions = extra_conditions;
		return this;
	}

	/**
	 * 自定义的额外触发条件，当概率触发无法满足需求时使用。返回true表示可以触发条件。设置了额外触发条件以后必须使用resolveTrigger(Event
	 * event,int...
	 * condition_indexes)判断能否触发，如果给出的索引超过数组长度则视为该索引判定为true。只要有一个条件不满足则返回false
	 * 
	 * @param related_arg       需要检测是否满足触发条件的相关参数
	 * @param condition_indexes 该事件对应的多个不同的条件检查方法的数组索引
	 * @return condition_indexes指定索引的全部条件均判定为true才触发，否则返回false
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final <T> boolean resolveExtraTriggerCondition(T related_arg, int... condition_indexes) {
		if (extra_trigger_conditions == null)
			return true;
		for (int idx : condition_indexes) {
			if (idx >= extra_trigger_conditions.length || ((TriggerCondition<T>) (extra_trigger_conditions[idx])).checkTriggerCondition(related_arg))
				continue;
			else
				return false;
		}
		return true;
	}

	public final boolean resolveTrigger() {
		return Random.check(trigger_chance);
	}

	/**
	 * 判断是否能触发setTriggerChances()规定的概率数组，其中索引indexes指定的概率均在判定中得到true，返回true，不应用额外条件
	 * 
	 * @return 是否所有indexes指定索引的概率均触发
	 */
	public final boolean resolveTrigger(int... indexes) {
		for (int idx : indexes)
			if (Random.check(trigger_chances[idx]))
				continue;
			else
				return false;
		return true;
	}

	/**
	 * 判断是否能触发setTriggerChances()规定的概率数组，其中索引indexes指定的概率均在判定中得到true，且应用setExtraTriggerCondition()设置的第一个额外条件
	 * 
	 * @param related_arg 需要判定触发的相关参数
	 * @return 是否触发
	 */
	@SuppressWarnings("unchecked")
	public final <T> boolean resolveTrigger(T related_arg) {
		return resolveTrigger() && ((TriggerCondition<T>) (extra_trigger_conditions[0])).checkTriggerCondition(related_arg);
	}

	/**
	 * 判断setTriggerChance()规定的概率数组，并应用setExtraTriggerCondition(Event
	 * event)设置的额外条件，其中额外条件的索引为condition_indexes
	 * 
	 * @param <T>
	 * @param related_arg       相关参数
	 * @param condition_indexes 额外条件的索引
	 * @return 是否触发
	 */
	@SafeVarargs
	public final <T> boolean resolveTrigger(T related_arg, int... condition_indexes) {
		return resolveTrigger() && resolveExtraTriggerCondition(related_arg, condition_indexes);// 优先调用子类的resolveTrigger()
	}

	public static final boolean resolveTrigger(double chance) {
		return Random.check(chance);
	}

	public static final boolean resolveTrigger(double... chances) {
		for (double chance : chances)
			if (Random.check(chance))
				continue;
			else
				return false;
		return true;
	}
}
