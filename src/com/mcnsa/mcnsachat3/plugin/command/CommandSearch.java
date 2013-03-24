package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "csearch", permission = "search", usage = "<player>", description = "views the channel a player is currently in")
public class CommandSearch implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandSearch(MCNSAChat3 plugin) {
		CommandSearch.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if(sArgs.length() < 1)
			return false;
	
		Player bukkitPlayer = Bukkit.getPlayer(sArgs);
		if(bukkitPlayer == null) {
			PluginUtil.send(sender, "&cPlayer not found.");
			return true;
		}
		ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
		ChatChannel chan = ChannelManager.getChannel(p.channel);
		
		PluginUtil.send(sender, PluginUtil.formatUser(p.name) + "&f is in channel " + chan.color + chan.name);
		
		return true;
	}
}
