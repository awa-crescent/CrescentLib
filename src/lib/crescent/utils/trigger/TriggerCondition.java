package lib.crescent.utils.trigger;

public interface TriggerCondition<T> {
	/**
	 * 自定义的额外触发条件，当概率触发无法满足需求时使用。返回true表示可以触发。设置了额外触发条件以后必须在Trigger中使用resolveTrigger(T
	 * related_arg)判断能否触发
	 * 
	 * @param related_arg 判定触发的相关参数，一般是事件Event
	 * @return
	 */

	public boolean checkTriggerCondition(T related_arg);
}
