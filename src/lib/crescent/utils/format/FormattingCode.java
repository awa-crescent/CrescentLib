package lib.crescent.utils.format;

//表示单个颜色或字体
public class FormattingCode {
	public final String prefix;// 前缀，只有内置颜色有前置
	public final String code;// 代码
	public final Type type;

	public static final char formatting_char = '\u00a7';

	enum Type {
		COLOR, FONT_STYLE
	}

	// 只能通过本类的工厂函数进行构造
	private FormattingCode(String prefix, String code, Type type) {
		this.prefix = prefix;
		this.code = code;
		this.type = type;
	}

	private FormattingCode(String prefix, String code) {
		this(prefix, code, Type.COLOR);
	}

	// 颜色
	public static final FormattingCode BLACK = new FormattingCode("&0", "#000000");
	public static final FormattingCode DARK_BLUE = new FormattingCode("&1", "#0000AA");
	public static final FormattingCode DARK_GREEN = new FormattingCode("&2", "#00AA00");
	public static final FormattingCode DARK_AQUA = new FormattingCode("&3", "#00AAAA");
	public static final FormattingCode DARK_RED = new FormattingCode("&4", "#AA0000");
	public static final FormattingCode DARK_PURPLE = new FormattingCode("&5", "#AA00AA");
	public static final FormattingCode GOLD = new FormattingCode("&6", "#FFAA00");
	public static final FormattingCode GRAY = new FormattingCode("&7", "#AAAAAA");
	public static final FormattingCode DARK_GREY = new FormattingCode("&8", "#555555");
	public static final FormattingCode BLUE = new FormattingCode("&9", "#5555FF");
	public static final FormattingCode GREEN = new FormattingCode("&a", "#55FF55");
	public static final FormattingCode AQUA = new FormattingCode("&b", "#55FFFF");
	public static final FormattingCode RED = new FormattingCode("&c", "#FF5555");
	public static final FormattingCode LIGHT_PURPLE = new FormattingCode("&d", "#FF55FF");
	public static final FormattingCode YELLOW = new FormattingCode("&e", "#FFFF55");
	public static final FormattingCode WHITE = new FormattingCode("&f", "#FFFFFF");
	public static final FormattingCode MINECOIN_GOLD = new FormattingCode("&g", "#DDD605");
	public static final FormattingCode MATERIAL_QUARTZ = new FormattingCode("&h", "#E3D4D1");
	public static final FormattingCode MATERIAL_IRON = new FormattingCode("&i", "#CECACA");
	public static final FormattingCode MATERIAL_NETHERITE = new FormattingCode("&j", "#443A3B");
	public static final FormattingCode MATERIAL_REDSTONE = new FormattingCode("&m", "#971607");
	public static final FormattingCode MINECOIN_COPPER = new FormattingCode("&n", "#B4684D");
	public static final FormattingCode MATERIAL_GOLD = new FormattingCode("&p", "#DEB12D");
	public static final FormattingCode MATERIAL_EMERAID = new FormattingCode("&q", "#47A036");
	public static final FormattingCode MATERIAL_DIAMOND = new FormattingCode("&s", "#2CBAAB");
	public static final FormattingCode MATERIAL_LAPIS = new FormattingCode("&t", "#21497B");
	public static final FormattingCode MATERIAL_AMETHYST = new FormattingCode("&u", "#9A5CC6");

	// 字体样式
	public static final FormattingCode OBFUSCATED = new FormattingCode("&k", "obfuscated", Type.FONT_STYLE);
	public static final FormattingCode BOLD = new FormattingCode("&l", "bold", Type.FONT_STYLE);
	public static final FormattingCode STRIKETHROUGH = new FormattingCode("&m", "strikethrough", Type.FONT_STYLE);
	public static final FormattingCode UNDERLINED = new FormattingCode("&u", "underlined", Type.FONT_STYLE);
	public static final FormattingCode ITALIC = new FormattingCode("&o", "italic", Type.FONT_STYLE);
	public static final FormattingCode RESET = new FormattingCode("&r", "reset", Type.FONT_STYLE);

	/**
	 * 返回预置样式，可能是颜色也可能是字体样式
	 * 
	 * @param name 预设名称
	 * @return 返回指定预设，不存在则返回null
	 */
	public static FormattingCode presets(String name) {
		switch (name) {
		case "black":
			return BLACK;
		case "dark_blue":
			return DARK_BLUE;
		case "dark_green":
			return DARK_GREEN;
		case "dark_aqua":
			return DARK_AQUA;
		case "dark_red":
			return DARK_RED;
		case "dark_purple":
			return DARK_PURPLE;
		case "gold":
			return GOLD;
		case "gray":
			return GRAY;
		case "dark_gray":
			return DARK_GREY;
		case "blue":
			return BLUE;
		case "green":
			return GREEN;
		case "aqua":
			return AQUA;
		case "red":
			return RED;
		case "light_purple":
			return LIGHT_PURPLE;
		case "yellow":
			return YELLOW;
		case "white":
			return WHITE;
		case "minecoin_gold":
			return MINECOIN_GOLD;
		case "material_quartz":
			return MATERIAL_QUARTZ;
		case "material_iron":
			return MATERIAL_IRON;
		case "material_netherite":
			return MATERIAL_NETHERITE;
		case "material_redstone":
			return MATERIAL_REDSTONE;
		case "material_copper":
			return MINECOIN_COPPER;
		case "material_gold":
			return MATERIAL_GOLD;
		case "material_emerald":
			return MATERIAL_EMERAID;
		case "material_diamond":
			return MATERIAL_DIAMOND;
		case "material_lapis":
			return MATERIAL_LAPIS;
		case "material_amethyst":
			return MATERIAL_AMETHYST;
		case "obfuscated":
			return OBFUSCATED;
		case "bold":
			return BOLD;
		case "strikethrough":
			return STRIKETHROUGH;
		case "underlined":
			return UNDERLINED;
		case "italic":
			return ITALIC;
		case "reset":
			return RESET;
		}
		return null;
	}

	public static FormattingCode hexColor(String hex) {
		return new FormattingCode(null, hex, Type.COLOR);
	}

	/**
	 * 
	 * @param R
	 * @param G
	 * @param B
	 * @return 返回十六进制颜色代码，以#开头
	 */
	public static FormattingCode rgbToHex(int r, int g, int b) {
		return new FormattingCode(null, String.format("#%02x%02x%02x", r, g, b), Type.COLOR);
	}
}
