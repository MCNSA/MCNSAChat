package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PlayerRefresh;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "crefresh", permission = "refresh", usage = "", description = "Refreshes the tab list")
public class CommandRefresh implements Command {
	public CommandRefresh(MCNSAChat3 plugin) {
		CommandChannel.plugin = plugin;
	}
	
	public Boolean handle(Player player, String sArgs) {
		PlayerRefresh.refreshTabList();
		PluginUtil.send(player.getName(), "Reloaded tab list");
		return true;
	}
}
