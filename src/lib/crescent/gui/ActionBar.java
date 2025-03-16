package lib.crescent.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar implements Listener {
	BaseComponent component;

	public ActionBar(String msg) {
		component = new TextComponent(msg);
	}

	/**
	 * 单独一次性添加一个玩家显示ActionBar
	 * 
	 * @param player 玩家
	 * @return
	 */
	public ActionBar addToPlayer(Player player) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
		return this;
	}
}
