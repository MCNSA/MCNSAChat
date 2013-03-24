package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "list", permission = "", description = "lists everyone who is online")
public class CommandList implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandList(MCNSAChat3 plugin) {
		CommandList.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		PluginUtil.send(sender, PluginUtil.getPlayerList());
		return true;
	}
}
