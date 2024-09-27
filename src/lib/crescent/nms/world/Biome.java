package lib.crescent.nms.world;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import lib.crescent.nms.NMSManipulator;
import lib.crescent.nms.core.RegistryManager;
import lib.crescent.nms.core.ResourceLocation;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeBase.ClimateSettings;
import net.minecraft.world.level.biome.BiomeBase.TemperatureModifier;

public class Biome {
	private static final String climate_settings_class_prefix = "net.minecraft.world.level.biome.Biome$ClimateSettings.";
	private static final String biome_class_prefix = "net.minecraft.world.level.biome.Biome.";

	private static final void setClimateSettingsFloatValue(ClimateSettings settings, String name, float value) {
		NMSManipulator.setFloatValue(settings, climate_settings_class_prefix + name, value);
	}

	private static final void setClimateSettingsBooleanValue(ClimateSettings settings, String name, boolean value) {
		NMSManipulator.setBooleanValue(settings, climate_settings_class_prefix + name, value);
	}

	private static final void setClimateSettingsObjectValue(ClimateSettings settings, String name, Object value) {
		NMSManipulator.setObjectValue(settings, climate_settings_class_prefix + name, value);
	}

	/**
	 * 设置群系基础温度，实际温度还与高度有关。当实际温度大于0.15时下雨，小于0.15时下雪
	 * 某个方块位置一旦读取了温度，其温度就存放在缓存中，以后访问该方块位置均会读取该缓存温度。
	 * 因此该方法应当在加载前调用。或在调用该方法后使用clearBiomeTemperatureCache()清除温度缓存
	 * 
	 * @param biome
	 * @param temperature
	 */
	public static final void setBiomeTemperature(ClimateSettings settings, float temperature) {
		setClimateSettingsFloatValue(settings, "temperature", temperature);
	}

	public static final void setBiomeTemperature(BiomeBase biome, float temperature) {
		setBiomeTemperature(biome.climateSettings, temperature);
	}

	public static final void setBiomeTemperature(String biome, float temperature) {
		setBiomeTemperature(getBiome(biome), temperature);
	}

	/**
	 * 清除温度缓存
	 * 
	 * @param biome
	 */
	@SuppressWarnings("unchecked")
	public static final void clearBiomeTemperatureCache(BiomeBase biome) {
		Long2FloatLinkedOpenHashMap temperature_cache = ((ThreadLocal<Long2FloatLinkedOpenHashMap>) (NMSManipulator.access(biome, biome_class_prefix + "temperatureCache"))).get();
		temperature_cache.clear();
	}

	public static final void clearBiomeTemperatureCache(String biome) {
		clearBiomeTemperatureCache(getBiome(biome));
	}

	/**
	 * 更改群系基础温度，并立即清除缓存以应用新的温度
	 * 
	 * @param biome
	 * @param temperature
	 */
	public static final void updateBiomeTemperature(BiomeBase biome, float temperature) {
		setBiomeTemperature(biome, temperature);
		clearBiomeTemperatureCache(biome);
	}

	public static final void updateBiomeTemperature(String biome, float temperature) {
		updateBiomeTemperature(getBiome(biome), temperature);
	}

	/**
	 * 设置群系的温度变化函数，实际温度为setBiomeTemperature()设置的基础温度经过该变化函数处理后的温度
	 * 
	 * @param biome
	 * @param temperatureModifier modifyTemperature()中传入方块位置和基础温度
	 */
	public static final void setBiomeTemperatureModifier(ClimateSettings settings, TemperatureModifier temperatureModifier) {
		setClimateSettingsObjectValue(settings, "temperatureModifier", temperatureModifier);
	}

	public static final void setBiomeTemperatureModifier(BiomeBase biome, TemperatureModifier temperatureModifier) {
		setBiomeTemperatureModifier(biome.climateSettings, temperatureModifier);
	}

	public static final void setBiomeTemperatureModifier(String biome, TemperatureModifier temperatureModifier) {
		setBiomeTemperatureModifier(getBiome(biome), temperatureModifier);
	}

	/**
	 * 设置群系是否有降水
	 * 
	 * @param biome
	 * @param hasPrecipitation
	 */
	public static final void setBiomeHasPrecipitation(ClimateSettings settings, boolean hasPrecipitation) {
		setClimateSettingsBooleanValue(settings, "hasPrecipitation", hasPrecipitation);
	}

	public static final void setBiomeHasPrecipitation(BiomeBase biome, boolean hasPrecipitation) {
		setBiomeHasPrecipitation(biome.climateSettings, hasPrecipitation);
	}

	public static final void setBiomeHasPrecipitation(String biome, boolean hasPrecipitation) {
		setBiomeHasPrecipitation(getBiome(biome), hasPrecipitation);
	}

	/**
	 * 设置群系downfall值。该值大于0.85时火焰将熄灭
	 * 
	 * @param biome
	 * @param downfall 取值0.0-1.0
	 */
	public static final void setBiomeDownfall(ClimateSettings settings, float downfall) {
		setClimateSettingsFloatValue(settings, "downfall", downfall);
	}

	public static final void setBiomeDownfall(BiomeBase biome, float downfall) {
		setBiomeDownfall(biome.climateSettings, downfall);
	}

	public static final void setBiomeDownfall(String biome, float downfall) {
		setBiomeDownfall(getBiome(biome), downfall);
	}

	/**
	 * 获取群系的环境效果
	 * 
	 * @param biome
	 * @return
	 */
	public static final BiomeSpecialEffects getBiomeSpecialEffects(BiomeBase biome) {
		return (BiomeSpecialEffects) NMSManipulator.getObjectValue(biome, biome_class_prefix + "specialEffects");
	}

	/**
	 * 通过ResourceLocation获取对应生物群系
	 * 
	 * @param biome
	 * @return
	 */
	public static final BiomeBase getBiome(MinecraftKey biome) {
		return RegistryManager.biome.get(biome);

	}

	public static final BiomeBase getBiome(ResourceLocation biome) {
		return RegistryManager.biome.get(biome.castToNMS());
	}

	/**
	 * 通过id获取生物群系
	 * 
	 * @param biome
	 * @return
	 */
	public static final BiomeBase getBiome(String biome) {
		return getBiome(ResourceLocation.getResourceLocationFromNamespacedID(biome));
	}
}
