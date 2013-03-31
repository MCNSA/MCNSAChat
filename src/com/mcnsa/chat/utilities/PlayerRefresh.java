package com.mcnsa.chat.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.mcnsa.chat.utilities.PluginUtil;

public class PlayerRefresh {
	
	public static void refreshTabList() {
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player: players) {
			String result = PluginUtil.color(PluginUtil.formatUser(player.getName()));
			if (result.length() > 16)
				result = result.substring(0, 16);
				player.setPlayerListName(result);
			}
		}
	}
