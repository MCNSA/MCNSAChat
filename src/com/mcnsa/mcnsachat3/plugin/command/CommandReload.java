package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "creload", permission = "reload", description = "reloads config and persist")
public class CommandReload implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandReload(MCNSAChat3 plugin) {
		CommandReload.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		plugin.reloadConfig();
		PluginUtil.send(sender, "Reloaded config.");
		
		return true;
	}
}
