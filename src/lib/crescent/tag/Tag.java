package lib.crescent.tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lib.crescent.item.ItemUtils;
import lib.crescent.nms.core.ResourceLocation;

/**
 * 包装Tag名称以及对应的物品
 */
public class Tag {
	public static final Tag AXES = new Tag("axes");
	public static final Tag PICKAXES = new Tag("pickaxes");
	public static final Tag SHOVELS = new Tag("shovels");
	public static final Tag HOES = new Tag("hoes");
	public static final Tag DURABILITY_ENCHANTABLE = new Tag("enchantable/durability");

	public static final Tag WEAPON_ENCHANTABLE = new Tag("enchantable/weapon");
	public static final Tag SHARP_WEAPON_ENCHANTABLE = new Tag("enchantable/sharp_weapon");
	public static final Tag BOW_ENCHANTABLE = new Tag("enchantable/bow");
	public static final Tag CROSSBOW_ENCHANTABLE = new Tag("enchantable/crossbow");
	public static final Tag SWORD_ENCHANTABLE = new Tag("enchantable/sword");
	public static final Tag ARMOR_ENCHANTABLE = new Tag("enchantable/armor");
	public static final Tag FOOT_ARMOR_ENCHANTABLE = new Tag("enchantable/foot_armor");
	public static final Tag LEG_ARMOR_ENCHANTABLE = new Tag("enchantable/leg_armor");
	public static final Tag CHEST_ARMOR_ENCHANTABLE = new Tag("enchantable/chest_armor");
	public static final Tag HEAD_ARMOR_ENCHANTABLE = new Tag("enchantable/head_armor");
	public static final Tag EQUIPPABLE_ENCHANTABLE = new Tag("enchantable/equippable");
	public static final Tag MINING_ENCHANTABLE = new Tag("enchantable/mining");
	public static final Tag MINING_LOOT_ENCHANTABLE = new Tag("enchantable/mining_loot");
	public static final Tag FISHING_ENCHANTABLE = new Tag("enchantable/fishing");
	public static final Tag TRIDENT_ENCHANTABLE = new Tag("enchantable/trident");
	public static final Tag MACE_ENCHANTABLE = new Tag("enchantable/mace");

	public static final Tag SHIELD_ENCHANTABLE = ItemUtils.createEnchantableItemTag("enchantable/shield", Set.of("minecraft:shield"));
	public static final Tag GENERIC_BOWS_ENCHANTABLE = ItemUtils.createEnchantableItemTag("enchantable/generic_bows", Set.of("minecraft:bow", "minecraft:crossbow"));

	protected ResourceLocation namespaced_tag;
	protected HashSet<String> members;

	public Tag(String namespace, String tag, Set<String> members) {
		this.namespaced_tag = new ResourceLocation(namespace, tag);
		this.members = members instanceof HashSet<String> hash_set_members ? hash_set_members : new HashSet<>(members);
	}

	public Tag(String namespace, String tag, String[] members) {
		this(namespace, tag, Set.of(members));
	}

	public Tag(String namespace, String tag) {
		this.namespaced_tag = new ResourceLocation(namespace, tag);
		this.members = null;
	}

	public Tag(String namespaced_tag, Set<String> members) {
		this(namespaced_tag);
		this.members = members instanceof HashSet<String> hash_set_members ? hash_set_members : new HashSet<>(members);
	}

	public Tag(String namespaced_tag) {
		this.namespaced_tag = new ResourceLocation(namespaced_tag);
	}

	public Tag(String tag, String[] members) {
		this(tag, Set.of(members));
	}

	public Tag(ResourceLocation namespaced_tag) {
		this.namespaced_tag = namespaced_tag;
		this.members = null;
	}

	public Tag(ResourceLocation namespaced_tag, Set<String> members) {
		this(namespaced_tag);
		this.members = members instanceof HashSet<String> hash_set_members ? hash_set_members : new HashSet<>(members);
	}

	public String getTag() {
		return namespaced_tag.getLocation();
	}

	public String getNamespace() {
		return namespaced_tag.getNamespace();
	}

	public String getNamespacedTag() {
		return namespaced_tag.toString();
	}

	public ResourceLocation getTagResourceLocation() {
		return namespaced_tag;
	}

	public HashSet<String> getMembers() {
		return members;
	}

	public boolean contains(String item) {
		return members.contains(item);
	}

	public boolean contains(String[] items) {
		for (String item : items)
			if (!members.contains(item))
				return false;
		return true;
	}

	public boolean contains(Collection<String> items) {
		return members.containsAll(items);
	}

	public Tag add(String item) {
		members.add(item);
		return this;
	}

	public Tag add(String[] items) {
		for (String item : items)
			members.add(item);
		return this;
	}

	public Tag add(Collection<String> items) {
		members.addAll(items);
		return this;
	}

	public Tag remove(String item) {
		members.remove(item);
		return this;
	}

	public Tag remove(String[] items) {
		for (String item : items)
			members.remove(item);
		return this;
	}

	public Tag remove(Collection<String> items) {
		members.removeAll(items);
		return this;
	}

	public boolean isEmpty() {
		return members.isEmpty();
	}
}
