package com.mcnsa.mcnsachat3.plugin.command;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "creconnect", permission = "reconnect", description = "breaks the connection to the chat server in hopes that it will be restored later")
public class CommandReconnect implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandReconnect(MCNSAChat3 plugin) {
		CommandReconnect.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if (MCNSAChat3.thread != null) {
			try {
				MCNSAChat3.thread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PluginUtil.send(sender, "Broke connection");
		return true;
	}
}
