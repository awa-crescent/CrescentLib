package lib.crescent.item;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import lib.crescent.enchantment.EnchantmentManager;
import lib.crescent.nms.core.TagUtils;
import lib.crescent.tag.Tag;

public class ItemUtils {

	public static void createItemTag(String namespaced_tag, Set<String> members) {
		TagUtils.appendItemTagMembers(namespaced_tag, members);
	}

	/**
	 * 注册新tag名并设置其下的成员列表。如果已经存在该tag则替换内容
	 * 
	 * @param tag_name tag的名称
	 * @param members  该tag下的物品
	 * @return
	 */
	public static void createItemTag(Tag item_tag) {
		createItemTag(item_tag.getNamespacedTag(), item_tag.getMembers());
	}

	/**
	 * 注册新tag名并设置其下的成员列表。如果已经存在该tag则替换内容，此tag的成员均可附魔
	 * 
	 * @param namespace tag的命名空间，一般是minecraft
	 * @param item_tag  tag的名称
	 * @param members   该tag下的物品
	 * @return 不具成员的ItemTag对象
	 */
	public static Tag createEnchantableItemTag(String namespaced_tag, Set<String> members) {
		createItemTag(namespaced_tag, members);
		EnchantmentManager.enchantable_items_set.add(members);
		return new Tag(namespaced_tag);
	}

	public static boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().hasEnchant(enchantment);
	}

	public static ItemStack getFurnaceOutput(ItemStack input) {
		FurnaceRecipe recipe = RecipeType.furnace_recipes.get(input.getType());
		if (recipe == null)
			return null;
		ItemStack result = recipe.getResult();
		result.setAmount((int) ((float) (input.getAmount()) / recipe.getInput().getAmount() * result.getAmount()));
		return result;
	}

	public static ItemStack getSpawnEgg(EntityType entity_type) {
		Material type = null;
		switch (entity_type) {
		case ALLAY:
			type = Material.ALLAY_SPAWN_EGG;
			break;
		case ARMADILLO:
			type = Material.ARMADILLO_SPAWN_EGG;
			break;
		case AXOLOTL:
			type = Material.AXOLOTL_SPAWN_EGG;
			break;
		case BAT:
			type = Material.BAT_SPAWN_EGG;
			break;
		case BEE:
			type = Material.BEE_SPAWN_EGG;
			break;
		case BLAZE:
			type = Material.BLAZE_SPAWN_EGG;
			break;
		case BOGGED:
			type = Material.BOGGED_SPAWN_EGG;
			break;
		case BREEZE:
			type = Material.BREEZE_SPAWN_EGG;
			break;
		case CAMEL:
			type = Material.CAMEL_SPAWN_EGG;
			break;
		case CAT:
			type = Material.CAT_SPAWN_EGG;
			break;
		case CAVE_SPIDER:
			type = Material.CAVE_SPIDER_SPAWN_EGG;
			break;
		case CHICKEN:
			type = Material.CHICKEN_SPAWN_EGG;
			break;
		case COD:
			type = Material.COD_SPAWN_EGG;
			break;
		case COW:
			type = Material.COW_SPAWN_EGG;
			break;
		case CREEPER:
			type = Material.CREEPER_SPAWN_EGG;
			break;
		case DOLPHIN:
			type = Material.DOLPHIN_SPAWN_EGG;
			break;
		case DONKEY:
			type = Material.DONKEY_SPAWN_EGG;
			break;
		case DROWNED:
			type = Material.DROWNED_SPAWN_EGG;
			break;
		case ELDER_GUARDIAN:
			type = Material.ELDER_GUARDIAN_SPAWN_EGG;
			break;
		case ENDERMAN:
			type = Material.ENDERMAN_SPAWN_EGG;
			break;
		case ENDERMITE:
			type = Material.ENDERMITE_SPAWN_EGG;
			break;
		case ENDER_DRAGON:
			type = Material.ENDER_DRAGON_SPAWN_EGG;
			break;
		case EVOKER:
			type = Material.EVOKER_SPAWN_EGG;
			break;
		case FOX:
			type = Material.FOX_SPAWN_EGG;
			break;
		case FROG:
			type = Material.FROG_SPAWN_EGG;
			break;
		case GHAST:
			type = Material.GHAST_SPAWN_EGG;
			break;
		case GLOW_SQUID:
			type = Material.GLOW_SQUID_SPAWN_EGG;
			break;
		case GOAT:
			type = Material.GOAT_SPAWN_EGG;
			break;
		case GUARDIAN:
			type = Material.GUARDIAN_SPAWN_EGG;
			break;
		case HOGLIN:
			type = Material.HOGLIN_SPAWN_EGG;
			break;
		case HORSE:
			type = Material.HORSE_SPAWN_EGG;
			break;
		case HUSK:
			type = Material.HUSK_SPAWN_EGG;
			break;
		case IRON_GOLEM:
			type = Material.IRON_GOLEM_SPAWN_EGG;
			break;
		case LLAMA:
			type = Material.LLAMA_SPAWN_EGG;
			break;
		case MAGMA_CUBE:
			type = Material.MAGMA_CUBE_SPAWN_EGG;
			break;
		case MOOSHROOM:
			type = Material.MOOSHROOM_SPAWN_EGG;
			break;
		case MULE:
			type = Material.MULE_SPAWN_EGG;
			break;
		case OCELOT:
			type = Material.OCELOT_SPAWN_EGG;
			break;
		case PANDA:
			type = Material.PANDA_SPAWN_EGG;
			break;
		case PARROT:
			type = Material.PARROT_SPAWN_EGG;
			break;
		case PHANTOM:
			type = Material.PHANTOM_SPAWN_EGG;
			break;
		case PIG:
			type = Material.PIG_SPAWN_EGG;
			break;
		case PIGLIN:
			type = Material.PIGLIN_SPAWN_EGG;
			break;
		case PIGLIN_BRUTE:
			type = Material.PIGLIN_BRUTE_SPAWN_EGG;
			break;
		case PILLAGER:
			type = Material.PILLAGER_SPAWN_EGG;
			break;
		case POLAR_BEAR:
			type = Material.POLAR_BEAR_SPAWN_EGG;
			break;
		case PUFFERFISH:
			type = Material.PUFFERFISH_SPAWN_EGG;
			break;
		case RABBIT:
			type = Material.RABBIT_SPAWN_EGG;
			break;
		case RAVAGER:
			type = Material.RAVAGER_SPAWN_EGG;
			break;
		case SALMON:
			type = Material.SALMON_SPAWN_EGG;
			break;
		case SHEEP:
			type = Material.SHEEP_SPAWN_EGG;
			break;
		case SHULKER:
			type = Material.SHULKER_SPAWN_EGG;
			break;
		case SILVERFISH:
			type = Material.SILVERFISH_SPAWN_EGG;
			break;
		case SKELETON:
			type = Material.SKELETON_SPAWN_EGG;
			break;
		case SKELETON_HORSE:
			type = Material.SKELETON_HORSE_SPAWN_EGG;
			break;
		case SLIME:
			type = Material.SLIME_SPAWN_EGG;
			break;
		case SNIFFER:
			type = Material.SNIFFER_SPAWN_EGG;
			break;
		case SNOW_GOLEM:
			type = Material.SNOW_GOLEM_SPAWN_EGG;
			break;
		case SPIDER:
			type = Material.SPIDER_SPAWN_EGG;
			break;
		case SQUID:
			type = Material.SQUID_SPAWN_EGG;
			break;
		case STRAY:
			type = Material.STRAY_SPAWN_EGG;
			break;
		case STRIDER:
			type = Material.STRIDER_SPAWN_EGG;
			break;
		case TADPOLE:
			type = Material.TADPOLE_SPAWN_EGG;
			break;
		case TRADER_LLAMA:
			type = Material.TRADER_LLAMA_SPAWN_EGG;
			break;
		case TROPICAL_FISH:
			type = Material.TROPICAL_FISH_SPAWN_EGG;
			break;
		case TURTLE:
			type = Material.TURTLE_SPAWN_EGG;
			break;
		case VEX:
			type = Material.VEX_SPAWN_EGG;
			break;
		case VILLAGER:
			type = Material.VILLAGER_SPAWN_EGG;
			break;
		case VINDICATOR:
			type = Material.VINDICATOR_SPAWN_EGG;
			break;
		case WANDERING_TRADER:
			type = Material.WANDERING_TRADER_SPAWN_EGG;
			break;
		case WARDEN:
			type = Material.WARDEN_SPAWN_EGG;
			break;
		case WITCH:
			type = Material.WITCH_SPAWN_EGG;
			break;
		case WITHER:
			type = Material.WITHER_SPAWN_EGG;
			break;
		case WITHER_SKELETON:
			type = Material.WITHER_SKELETON_SPAWN_EGG;
			break;
		case WOLF:
			type = Material.WOLF_SPAWN_EGG;
			break;
		case ZOGLIN:
			type = Material.ZOGLIN_SPAWN_EGG;
			break;
		case ZOMBIE:
			type = Material.ZOMBIE_SPAWN_EGG;
			break;
		case ZOMBIE_HORSE:
			type = Material.ZOMBIE_HORSE_SPAWN_EGG;
			break;
		case ZOMBIE_VILLAGER:
			type = Material.ZOMBIE_VILLAGER_SPAWN_EGG;
			break;
		case ZOMBIFIED_PIGLIN:
			type = Material.ZOMBIFIED_PIGLIN_SPAWN_EGG;
			break;
		default:
			return null;
		}
		return new ItemStack(type, 1);
	}
}
