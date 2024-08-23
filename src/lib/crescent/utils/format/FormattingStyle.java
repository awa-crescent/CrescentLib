package lib.crescent.utils.format;

import java.util.ArrayList;

//表示颜色及字体的组合
public class FormattingStyle {
	public enum FormattingType {
		PREFIX, JSON
	}

	public FormattingType formatting_type = FormattingType.JSON;
	protected FormattingCode color = null;// 默认不设置字体颜色。实际颜色由MC决定，通常是白色
	protected ArrayList<FormattingCode> font_styles = new ArrayList<FormattingCode>();

	// 传入颜色及字体样式，顺序可随意。如果传入超过两个则只生效最后传入的颜色，但字体样式可以有多个。只要其中有一个RESET就抛弃该RESET字体样式前的所有字体样式
	public FormattingStyle(FormattingCode... code_arr) {
		for (FormattingCode fmt_code : code_arr) {
			switch (fmt_code.type) {
			case COLOR:
				setColor(fmt_code);
				break;
			case FONT_STYLE:
				addFontStyle(fmt_code);
				break;
			}
		}
	}

	/**
	 * 
	 * @param color_code 颜色十六进制代码
	 * @param font_style 字体样式名称
	 */
	public FormattingStyle(String color_code, String font_style) {
		this(FormattingCode.hexColor(color_code), FormattingCode.presets(font_style));
	}

	// 设置颜色，可以设置null表示回归默认颜色
	public FormattingStyle setColor(FormattingCode c) {
		color = c;
		return this;
	}

	// 立即清空字体样式
	public FormattingStyle clearFontStyle() {
		font_styles.clear();
		return this;
	}

	// 添加字体样式，如果添加RESET则立即清空字体样式，并且不会储存RESET样式
	public FormattingStyle addFontStyle(FormattingCode c) {
		if (c.code == FormattingCode.RESET.code)// 当前字体样式是RESET就清除前面所有样式
			clearFontStyle();
		if (!font_styles.contains(c))// 同样的字体样式如果存在则忽略
			this.font_styles.add(c);
		return this;
	}

	// 移除字体样式
	public FormattingStyle removeFontStyle(FormattingCode c) {
		this.font_styles.remove(c);
		return this;
	}

	/**
	 * 根据this.formatting_type选择前缀或是JSON进行格式化
	 * 
	 * @param str 要格式化的字符串
	 * @return 返回格式化好的字符串
	 */
	public String formatString(String str) {
		switch (formatting_type) {
		case FormattingType.PREFIX:
			return formatStringPrefix(str);
		case FormattingType.JSON:
			return formatStringJSON(str);
		}
		return str;
	}

	/**
	 * 仅使用前缀prefix格式化字符串
	 * 
	 * @param str   要格式化的字符串
	 * @param style 样式
	 * @return 返回前缀格式化的字符串
	 */
	public String formatStringPrefix(String str) {
		StringBuilder sb = new StringBuilder(1024);
		for (FormattingCode font_style : font_styles)
			sb.append(font_style.prefix);
		String sb_str = sb.toString();
		return (sb_str == null ? "" : sb_str + (color == null || color.prefix == null ? "" : color.prefix) + str).replace('&', FormattingCode.formatting_char);
	}

	/**
	 * 
	 * @param str
	 * @param style
	 * @return 返回JSON格式化的字符串，以{}括起来
	 */
	public String formatStringJSON(String str) {
		StringBuilder sb = new StringBuilder(1024);
		sb.append("{\"text\":\"" + str + "\"");//// 添加text属性
		if (color != null)
			sb.append(",\"color\":\"" + color.code + "\"");// 如果有指定颜色就添加color属性
		for (FormattingCode font_style : font_styles) {
			if (font_style.code == FormattingCode.OBFUSCATED.code)
				sb.append(",\"obfuscated\":true");
			else if (font_style.code == FormattingCode.BOLD.code)
				sb.append(",\"bold\":true");
			else if (font_style.code == FormattingCode.STRIKETHROUGH.code)
				sb.append(",\"strikethrough\":true");
			else if (font_style.code == FormattingCode.UNDERLINED.code)
				sb.append(",\"underlined\":true");
			else if (font_style.code == FormattingCode.ITALIC.code)
				sb.append(",\"italic\":true");
		}
		sb.append('}');
		return sb.toString();
	}

	public void setColor(String hex_color) {
		setColor(FormattingCode.hexColor(hex_color));
	}
}