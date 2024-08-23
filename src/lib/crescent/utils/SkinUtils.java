package lib.crescent.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lib.crescent.Reflect;
import lib.crescent.utils.format.FormattingStyle;

//https://github.com/deanveloper/SkullCreator/blob/main/src/main/java/day/dean/skullcreator/SkullCreator.java
public class SkinUtils {
	/**
	 * 编码皮肤URL为Base64字符串以供设置
	 * 
	 * @param url 要编码的URL，为Mojang格式的URL，指向皮肤文件
	 * @return 编码出的Base64字符串
	 */
	public static String encodeSkinFromURL(String url) {
		try {
			return Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + new URI(url).toString() + "\"}}}").getBytes());
		} catch (URISyntaxException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "SkinUtils cannot find URL " + url, ex);
		}
		return null;
	}

	/**
	 * 生成自定义的玩家信息，可用于玩家头颅皮肤自定义
	 * 
	 * @param profile_name 玩家名称
	 * @param skin_url     玩家皮肤URL
	 * @return GameProfile玩家信息
	 */
	public static GameProfile generateProfileFromSkinURL(String profile_name, String skin_url) {
		String base64 = encodeSkinFromURL(skin_url);
		UUID uuid = new UUID(profile_name.hashCode(), base64.hashCode());
		GameProfile profile = new GameProfile(uuid, profile_name);
		profile.getProperties().put("textures", new Property("textures", base64));
		return profile;
	}

	/**
	 * 根据玩家UUID生成对应的头颅，头颅显示名称为玩家名称+name_suffix
	 * 
	 * @param uuid        玩家UUID
	 * @param name_suffix 玩家头颅名称的后缀
	 * @param name_style  头颅显示名称的格式化样式，设置为null表示默认样式
	 * @return 生成的头颅物品
	 */
	public static ItemStack generatePlayerSkull(UUID uuid, String name_suffix, FormattingStyle name_style) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skull_meta = (SkullMeta) skull.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		skull_meta.setOwningPlayer(player);
		skull_meta.setDisplayName(name_style == null ? player.getName() + name_suffix : name_style.formatStringJSON(player.getName() + name_suffix));
		skull.setItemMeta(skull_meta);
		return skull;
	}

	public static ItemStack generatePlayerSkull(UUID uuid, String name_suffix) {
		return generatePlayerSkull(uuid, name_suffix, null);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack generatePlayerSkull(String player_name, String name_suffix, FormattingStyle name_style) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skull_meta = (SkullMeta) skull.getItemMeta();
		skull_meta.setOwningPlayer(Bukkit.getOfflinePlayer(player_name));
		skull_meta.setDisplayName(name_style == null ? player_name + name_suffix : name_style.formatStringJSON(player_name + name_suffix));
		skull.setItemMeta(skull_meta);
		return skull;
	}

	public static ItemStack generatePlayerSkull(String player_name, String name_suffix) {
		return generatePlayerSkull(player_name, name_suffix, null);
	}

	/**
	 * 生成自定义皮肤和显示名称的头颅
	 * 
	 * @param display_name 头颅显示名称（全称）
	 * @param skin_url     Mojang格式的URL，指向皮肤文件
	 * @param name_style   显示名称的样式
	 * @return 头颅物品
	 */
	public static ItemStack generatePlayerSkullFromURL(String display_name, String skin_url, FormattingStyle name_style) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skull_meta = (SkullMeta) skull.getItemMeta();
		skull_meta.setDisplayName(name_style == null ? display_name : name_style.formatStringJSON(display_name));
		Reflect.setValue(skull_meta, "profile", generateProfileFromSkinURL("player-skull-skin", skin_url));
		skull.setItemMeta(skull_meta);
		return skull;
	}
}
