package lib.crescent.nms.world;

import java.util.OptionalLong;

import org.bukkit.World.Environment;

import lib.crescent.nms.NMSManipulator;
import lib.crescent.nms.core.RegistryManager;
import lib.crescent.nms.core.ResourceLocation;
import lib.crescent.nms.world.LevelStem.Type;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionManager;

public class DimensionType {

	public static final DimensionManager TYPE_OVERWORLD;// DimensionType,可在wiki上查看各项参数的作用
	public static final DimensionManager TYPE_NETHER;
	public static final DimensionManager TYPE_END;
	public static final DimensionManager TYPE_OVERWORLD_CAVES;

	static {
		TYPE_OVERWORLD = RegistryManager.dimension_type.get(BuiltinDimensionTypes.OVERWORLD);
		TYPE_NETHER = RegistryManager.dimension_type.get(BuiltinDimensionTypes.NETHER);
		TYPE_END = RegistryManager.dimension_type.get(BuiltinDimensionTypes.END);
		TYPE_OVERWORLD_CAVES = RegistryManager.dimension_type.get(BuiltinDimensionTypes.OVERWORLD_CAVES);
	}

	public static DimensionManager getDimensionType(Type type) {
		switch (type) {
		case END:
			return TYPE_END;
		case NETHER:
			return TYPE_NETHER;
		case OVERWORLD:
			return TYPE_OVERWORLD;
		case OVERWORLD_CAVES:
			return TYPE_OVERWORLD_CAVES;
		default:
			return null;
		}
	}

	public static DimensionManager getDimensionType(Environment type) {
		switch (type) {
		case THE_END:
			return TYPE_END;
		case NETHER:
			return TYPE_NETHER;
		case NORMAL:
			return TYPE_OVERWORLD;
		default:
			return null;
		}
	}

	public static DimensionManager getDimensionType(org.bukkit.World world) {
		return getDimensionType(world.getEnvironment());
	}

	// 修改变量值的内部方法
	private static final String class_prefix = "net.minecraft.world.level.dimension.DimensionType.";

	private static void setDimensionManagerBooleanValue(DimensionManager dimension, String name, boolean value) {
		NMSManipulator.setBooleanValue(dimension, class_prefix + name, value);
	}

	private static void setDimensionManagerIntValue(DimensionManager dimension, String name, int value) {
		NMSManipulator.setIntValue(dimension, class_prefix + name, value);
	}

	private static void setDimensionManagerFloatValue(DimensionManager dimension, String name, float value) {
		NMSManipulator.setFloatValue(dimension, class_prefix + name, value);
	}

	private static void setDimensionManagerDoubleValue(DimensionManager dimension, String name, double value) {
		NMSManipulator.setDoubleValue(dimension, class_prefix + name, value);
	}

	private static void setDimensionManagerObjectValue(DimensionManager dimension, String name, Object value) {
		NMSManipulator.setObjectValue(dimension, class_prefix + name, value);
	}

	/**
	 * 是否禁止自然光照、是否天气循环、阳光传感器能否工作，以及生成幻翼
	 * 
	 * @param dimension
	 * @param hasSkyLight
	 */
	public static void setHasSkyLight(DimensionManager dimension, boolean hasSkyLight) {
		setDimensionManagerBooleanValue(dimension, "hasSkyLight", hasSkyLight);
	}

	public static void setHasSkyLight(Type dimension, boolean hasSkyLight) {
		setHasSkyLight(getDimensionType(dimension), hasSkyLight);
	}

	/**
	 * 地狱特有的特性。设置能否放置水桶、岩浆流速加快范围变广、冰块融化和打破冰块是否出水源，滴水石锥滴落的是否是岩浆、实体在岩浆中的呼吸时长
	 * 
	 * @param dimension
	 * @param ultraWarm
	 */
	public static void setUltraWarm(DimensionManager dimension, boolean ultraWarm) {
		setDimensionManagerBooleanValue(dimension, "ultraWarm", ultraWarm);
	}

	public static void setUltraWarm(Type dimension, boolean ultraWarm) {
		setUltraWarm(getDimensionType(dimension), ultraWarm);
	}

	/**
	 * 地狱特有的特性。是否有天花板，如果有则会调整重生点位置在天花板下，刷怪笼刷出的怪也会在天花板下，地图物品显示的世界地图也会变化，只显示天花板下的表面区域
	 * 
	 * @param dimension
	 * @param hasCeiling
	 */
	public static void setHasCeiling(DimensionManager dimension, boolean hasCeiling) {
		setDimensionManagerBooleanValue(dimension, "hasCeiling", hasCeiling);
	}

	public static void setHasCeiling(Type dimension, boolean hasCeiling) {
		setHasCeiling(getDimensionType(dimension), hasCeiling);
	}

	/**
	 * 能否自然生成生物和指南针指向重生点、上床睡觉
	 * 
	 * @param dimension
	 * @param natural
	 */
	public static void setNatural(DimensionManager dimension, boolean natural) {
		setDimensionManagerBooleanValue(dimension, "natural", natural);
	}

	public static void setNatural(Type dimension, boolean natural) {
		setNatural(getDimensionType(dimension), natural);
	}

	/**
	 * 设置床能否使用。不能使用的话就无法用床设置出生点，使用床还会爆炸
	 * 
	 * @param dimension
	 * @param bedWorks
	 */
	public static void setBedWorks(DimensionManager dimension, boolean bedWorks) {
		setDimensionManagerBooleanValue(dimension, "bedWorks", bedWorks);
	}

	public static void setBedWorks(Type dimension, boolean bedWorks) {
		setBedWorks(getDimensionType(dimension), bedWorks);
	}

	/**
	 * 设置重生锚是否可以使用
	 * 
	 * @param dimension
	 * @param respawnAnchorWorks
	 */
	public static void setRespawnAnchorWorks(DimensionManager dimension, boolean respawnAnchorWorks) {
		setDimensionManagerBooleanValue(dimension, "respawnAnchorWorks", respawnAnchorWorks);
	}

	public static void setRespawnAnchorWorks(Type dimension, boolean respawnAnchorWorks) {
		setRespawnAnchorWorks(getDimensionType(dimension), respawnAnchorWorks);
	}

	/**
	 * 设置建造的最低y轴高度
	 * 
	 * @param dimension
	 * @param minY
	 */
	public static void setMinY(DimensionManager dimension, int minY) {
		setDimensionManagerIntValue(dimension, "minY", minY);
	}

	public static void setMinY(Type dimension, int minY) {
		setMinY(getDimensionType(dimension), minY);
	}

	/**
	 * 设置世界高度，也是生物生成、玩家建造、地形生成的最大高度，从minY开始计算的绝对高度
	 * 
	 * @param dimension
	 * @param height
	 */
	public static void setHeight(DimensionManager dimension, int height) {
		setDimensionManagerIntValue(dimension, "height", height);
	}

	public static void setHeight(Type dimension, int height) {
		setHeight(getDimensionType(dimension), height);
	}

	/**
	 * 地狱特有的特性，坐标缩放尺度。世界中心的坐标、世界间传送时的目的坐标均会缩放
	 * 
	 * @param dimension
	 * @param coordinateScale
	 */
	public static void setCoordinateScale(DimensionManager dimension, double coordinateScale) {
		setDimensionManagerDoubleValue(dimension, "coordinateScale", coordinateScale);
	}

	public static void setCoordinateScale(Type dimension, double coordinateScale) {
		setCoordinateScale(getDimensionType(dimension), coordinateScale);
	}

	/**
	 * 设置逻辑高度。会影响紫颂果的传送高度、（地狱）传送门生成的y轴高度，这个高度要保证logicalHeight-minY<=height，通常设置为minY+height
	 * 
	 * @param dimension
	 * @param logicalHeight
	 */
	public static void setLogicalHeight(DimensionManager dimension, int logicalHeight) {
		setDimensionManagerFloatValue(dimension, "logicalHeight", logicalHeight);
	}

	public static void setLogicalHeight(Type dimension, int logicalHeight) {
		setLogicalHeight(getDimensionType(dimension), logicalHeight);
	}

	/**
	 * 设置世界高度，同时设置最低坐标minY，以及世界的高度height和逻辑高度logicalHeight。其中logicalHeight包含整个世界
	 * 
	 * @param dimension
	 * @param minY
	 * @param height
	 */
	public static void setDimensionHeight(DimensionManager dimension, int minY, int height) {
		setMinY(dimension, minY);
		setHeight(dimension, height);
		setLogicalHeight(dimension, minY + height);
	}

	public static void setDimensionHeight(Type dimension, int minY, int height) {
		setDimensionHeight(getDimensionType(dimension), minY, height);
	}

	/**
	 * 这是一个MagicNumber，和目标方块的光照强度一起用于线性插值获取实际光照强度，Mojang标注了@Deprecated，目前只有海龟的行为逻辑与此有关
	 * 
	 * @param dimension
	 * @param ambientLight
	 */
	public static void setAmbientLight(DimensionManager dimension, float ambientLight) {
		setDimensionManagerFloatValue(dimension, "ambientLight", ambientLight);
	}

	public static void setAmbientLight(Type dimension, float ambientLight) {
		setAmbientLight(getDimensionType(dimension), ambientLight);
	}

	/**
	 * 设置是否固定时间，一天时间的有效范围是0-24000，超出范围则不固定时间
	 * 
	 * @param dimension
	 * @param fixedTime
	 */
	public static void setFixedTime(DimensionManager dimension, long fixedTime) {
		setDimensionManagerObjectValue(dimension, "fixedTime", (fixedTime < 0 || fixedTime > 24000) ? OptionalLong.empty() : OptionalLong.of(fixedTime));
	}

	public static void setFixedTime(Type dimension, long fixedTime) {
		setFixedTime(getDimensionType(dimension), fixedTime);
	}

	/**
	 * 设置世界的天空、雾效果
	 * 
	 * @param dimension
	 * @param effectsLocation
	 */
	public static void setEffectsLocation(DimensionManager dimension, String effectsLocation) {
		setDimensionManagerObjectValue(dimension, "effectsLocation", ResourceLocation.getResourceLocationFromNamespacedID(effectsLocation));
	}

	public static void setEffectsLocation(Type dimension, String effectsLocation) {
		setEffectsLocation(getDimensionType(dimension), effectsLocation);
	}

	public static void setEffectsLocation(Type dimension, Type effects_type) {
		switch (effects_type) {
		case END:
			setEffectsLocation(dimension, "minecraft:the_end");
			break;
		case NETHER:
			setEffectsLocation(dimension, "minecraft:the_nether");
			break;
		case OVERWORLD:
		case OVERWORLD_CAVES:
			setEffectsLocation(dimension, "minecraft:overworld");
			break;
		default:
			break;
		}
	}

	public static void setMoonBrightnessPerPhase(DimensionManager dimension, float[] MOON_BRIGHTNESS_PER_PHASE) {
		NMSManipulator.setObjectValue(dimension, "net.minecraft.world.level.dimension.DimensionType.MOON_BRIGHTNESS_PER_PHASE", MOON_BRIGHTNESS_PER_PHASE);
	}

	public static void setMoonBrightnessPerPhase(Type dimension, float[] MOON_BRIGHTNESS_PER_PHASE) {
		setMoonBrightnessPerPhase(getDimensionType(dimension), MOON_BRIGHTNESS_PER_PHASE);
	}

	/**
	 * 通过ResourceLocation获取对应维度类型
	 * 
	 * @param biome
	 * @return
	 */
	public static final DimensionManager getDimensionType(MinecraftKey dim) {
		return RegistryManager.dimension_type.get(dim);
	}

	public static final DimensionManager getDimensionType(ResourceLocation dim) {
		return getDimensionType(dim.castToNMS());
	}

	/**
	 * 通过id获取维度类型
	 * 
	 * @param dim
	 * @return
	 */
	public static final DimensionManager getDimensionType(String dim) {
		return getDimensionType(ResourceLocation.getResourceLocationFromNamespacedID(dim));
	}
}
