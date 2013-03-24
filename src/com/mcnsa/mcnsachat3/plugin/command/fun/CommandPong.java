package com.mcnsa.mcnsachat3.plugin.command.fun;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;
import com.mcnsa.mcnsachat3.plugin.command.Command;

@Command.CommandInfo(alias = "pong", permission = "fun", usage = "", description = "")
public class CommandPong implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandPong(MCNSAChat3 plugin) {
		CommandPong.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		PluginUtil.send(sender, "I hear " + sender.getName() + " likes cute asian boys.");
		return true;
	}
}
