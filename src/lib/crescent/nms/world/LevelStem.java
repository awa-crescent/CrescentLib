package lib.crescent.nms.world;

import org.bukkit.World.Environment;

import lib.crescent.nms.core.RegistryManager;
import lib.crescent.nms.core.ResourceLocation;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.dimension.WorldDimension;

public class LevelStem {
	public static enum Type {
		OVERWORLD, NETHER, END, OVERWORLD_CAVES
	}

	// DIM_OVERWORLD.type().value() == TYPE_OVERWORLD 两者是同一个对象
	public static final WorldDimension DIM_OVERWORLD;// Dimension种类，包括了DimensionType和对应的地形生成器。net.minecraft.world.level.dimension.LevelStem
	public static final WorldDimension DIM_NETHER;
	public static final WorldDimension DIM_END;

	static {
		DIM_OVERWORLD = RegistryManager.level_stem.get(WorldDimension.OVERWORLD);
		DIM_NETHER = RegistryManager.level_stem.get(WorldDimension.NETHER);
		DIM_END = RegistryManager.level_stem.get(WorldDimension.END);
	}

	public static WorldDimension getLevelStem(Type type) {
		switch (type) {
		case END:
			return DIM_END;
		case NETHER:
			return DIM_NETHER;
		case OVERWORLD:
		case OVERWORLD_CAVES:
			return DIM_OVERWORLD;
		default:
			return null;
		}
	}

	public static WorldDimension getLevelStem(Environment type) {
		switch (type) {
		case THE_END:
			return DIM_END;
		case NETHER:
			return DIM_NETHER;
		case NORMAL:
			return DIM_OVERWORLD;
		default:
			return null;
		}
	}

	public static WorldDimension getLevelStem(org.bukkit.World world) {
		return getLevelStem(world.getEnvironment());
	}

	/**
	 * 通过ResourceLocation获取对应维度stem
	 * 
	 * @param biome
	 * @return
	 */
	public static final WorldDimension getLevelStem(MinecraftKey dim) {
		return RegistryManager.level_stem.get(dim);
	}

	public static final WorldDimension getLevelStem(ResourceLocation dim) {
		return getLevelStem(dim.castToNMS());
	}

	/**
	 * 通过id获取维度stem
	 * 
	 * @param dim
	 * @return
	 */
	public static final WorldDimension getLevelStem(String dim) {
		return getLevelStem(ResourceLocation.getResourceLocationFromNamespacedID(dim));
	}
}
