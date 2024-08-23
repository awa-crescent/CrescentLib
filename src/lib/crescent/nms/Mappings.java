package lib.crescent.nms;

/**
 * 
 * 统一使用Mojang Mappings
 *
 */
public abstract class Mappings {
	public abstract class net {
		public abstract class minecraft {
			public abstract class core {
				public abstract class MappedRegistry {
					public static final String frozen = "l";
					public static final String unregisteredIntrusiveHolders = "m";
				}

				public abstract class Holder {
					public abstract class Reference {
						public static final String tags = "b";
					}
				}

				public abstract class HolderSet {
					public static String HolderSet_Named = "Named";

					public abstract class Named {
						public static final String contents = "c";
					}

					public static String HolderSet_Direct = "a";

					public abstract class Direct {
						public static final String EMPTY = "a";
						public static final String contents = "b";
						public static final String contentsSet = "c";
					}

					public static String HolderSet_ListBacked = "b";
				}
			}

			public abstract class world {
				public abstract class item {
					public abstract class enchantment {
						public abstract class Enchantment {
							public static final String description = "e";
							public static final String definition = "f";
							public static final String exclusiveSet = "g";
							public static final String effects = "h";
							public static final String MAX_LEVEL = "a";

							public abstract class EnchantmentDefinition {
								public static final String supportedItems = "b";
								public static final String primaryItems = "c";
								public static final String weight = "d";
								public static final String maxLevel = "e";
								public static final String minCost = "f";
								public static final String maxCost = "g";
								public static final String anvilCost = "h";
								public static final String slots = "i";
							}
						}
					}
				}
			}
		}
	}
}
