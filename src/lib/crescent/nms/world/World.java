package lib.crescent.nms.world;

import java.util.OptionalLong;

import lib.crescent.Manipulator;
import lib.crescent.nms.MappingsEntry;
import lib.crescent.nms.core.RegistryManager;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionManager;

public class World {
	public static enum Type {
		OVERWORLD, NETHER, END, OVERWORLD_CAVES
	}

	public static final DimensionManager OVERWORLD;
	public static final DimensionManager NETHER;
	public static final DimensionManager END;
	public static final DimensionManager OVERWORLD_CAVES;
	public static Class<?> World;

	static {
		OVERWORLD = RegistryManager.dimension_manager_registry.get(BuiltinDimensionTypes.OVERWORLD);
		NETHER = RegistryManager.dimension_manager_registry.get(BuiltinDimensionTypes.NETHER);
		END = RegistryManager.dimension_manager_registry.get(BuiltinDimensionTypes.END);
		OVERWORLD_CAVES = RegistryManager.dimension_manager_registry.get(BuiltinDimensionTypes.OVERWORLD_CAVES);
		try {
			World = Class.forName("net.minecraft.world.level.World");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public static DimensionManager getDimensionManager(Type type) {
		switch (type) {
		case END:
			return END;
		case NETHER:
			return NETHER;
		case OVERWORLD:
			return OVERWORLD;
		case OVERWORLD_CAVES:
			return OVERWORLD_CAVES;
		default:
			return null;
		}
	}

	public static void setTicksPerDay(int ticks) {
		Manipulator.setIntValue(World, MappingsEntry.getObfuscatedName("net.minecraft.world.level.World.TICKS_PER_DAY"), ticks);
	}

	public static void setHasSkyLight(Type dimension, boolean hasSkyLight) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.hasSkyLight"), hasSkyLight);
	}

	public static void setUltraWarm(Type dimension, boolean ultraWarm) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.ultraWarm"), ultraWarm);
	}

	public static void setHasCeiling(Type dimension, boolean hasCeiling) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.hasCeiling"), hasCeiling);
	}

	public static void setNatural(Type dimension, boolean natural) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.natural"), natural);
	}

	public static void setCoordinateScale(Type dimension, double coordinateScale) {
		Manipulator.setDoubleValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.coordinateScale"), coordinateScale);
	}

	public static void setBedWorks(Type dimension, boolean bedWorks) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.bedWorks"), bedWorks);
	}

	public static void setRespawnAnchorWorks(Type dimension, boolean respawnAnchorWorks) {
		Manipulator.setBooleanValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.respawnAnchorWorks"), respawnAnchorWorks);
	}

	public static void setMinY(Type dimension, int minY) {
		Manipulator.setIntValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.minY"), minY);
	}

	public static void setHeight(Type dimension, int height) {
		Manipulator.setIntValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.height"), height);
	}

	public static void setLogicalHeight(Type dimension, float ambientLight) {
		Manipulator.setFloatValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.ambientLight"), ambientLight);
	}

	public static void setFixedTime(Type dimension, long fixedTime) {
		Manipulator.setObjectValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.fixedTime"), (fixedTime < 0 || fixedTime > 24000) ? OptionalLong.empty() : OptionalLong.of(fixedTime));
	}

	public static void setFixedTime(Type dimension, float[] MOON_BRIGHTNESS_PER_PHASE) {
		Manipulator.setObjectValue(getDimensionManager(dimension), MappingsEntry.getObfuscatedName("net.minecraft.world.level.dimension.DimensionType.MOON_BRIGHTNESS_PER_PHASE"), MOON_BRIGHTNESS_PER_PHASE);
	}
}
