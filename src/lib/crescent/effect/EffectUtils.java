package lib.crescent.effect;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class EffectUtils {

	/**
	 * 移除实体所有原版正面效果
	 * 
	 * @param entity
	 * @return
	 */
	public static Collection<PotionEffect> removeAllVanillaPositivePotionEffects(LivingEntity entity) {
		if (entity == null)
			return null;
		Collection<PotionEffect> removed = new ArrayList<>();
		Collection<PotionEffect> effects = entity.getActivePotionEffects();
		for (PotionEffect effect : effects)
			if (EffectUtils.isVanillaPositive(effect)) {
				removed.add(effect);
				entity.removePotionEffect(effect.getType());
			}
		return removed;
	}

	public static Collection<PotionEffect> removeAllVanillaNegativePotionEffects(LivingEntity entity) {
		if (entity == null)
			return null;
		Collection<PotionEffect> removed = new ArrayList<>();
		Collection<PotionEffect> effects = entity.getActivePotionEffects();
		for (PotionEffect effect : effects)
			if (EffectUtils.isVanillaNegative(effect)) {
				removed.add(effect);
				entity.removePotionEffect(effect.getType());
			}
		return removed;
	}

	/**
	 * 判断药水是不是原版正面效果
	 * 
	 * @param effect 要判断的效果
	 * @return 如果不是原版的，或者不是原版的正面效果则返回false
	 */
	public static boolean isVanillaPositive(PotionEffect effect) {
		if (effect.getType().getKey().getNamespace() != "minecraft")
			return false;
		String type_name = effect.getType().getKey().getKey();
		switch (type_name) {
		case "speed":
		case "haste":
		case "strength":
		case "instant_health":
		case "jump_boost":
		case "regeneration":
		case "resistance":
		case "fire_resistance":
		case "water_breathing":
		case "invisibility":
		case "night_vision":
		case "health_boost":
		case "absorption":
		case "saturation":
		case "glowing":
		case "luck":
		case "slow_falling":
		case "conduit_power":
		case "dolphins_grace":
		case "hero_of_the_village":
		case "wind_charged":
		case "weaving":
			return true;
		}
		return false;
	}

	/**
	 * 判断药水是不是原版负面效果
	 * 
	 * @param effect 要判断的效果
	 * @return 如果不是原版的，或者不是原版的负面效果则返回false
	 */
	public static boolean isVanillaNegative(PotionEffect effect) {
		if (effect.getType().getKey().getNamespace() != "minecraft")
			return false;
		String type_name = effect.getType().getKey().getKey();
		switch (type_name) {
		case "slowness":
		case "mining_fatigue":
		case "instant_damage":
		case "nausea":
		case "blindness":
		case "hunger":
		case "weakness":
		case "poison":
		case "wither":
		case "levitation":
		case "unluck":
		case "bad_omen":
		case "darkness":
		case "trial_omen":
		case "raid_omen":
		case "oozing":
		case "infested":
			return true;
		}
		return false;
	}
}
