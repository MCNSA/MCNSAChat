package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;
import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.packets.ChannelUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "cname", permission = "name", usage = "<channel> <name>", description = "changes the name of a channel")
public class CommandName implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandName(MCNSAChat3 plugin) {
		CommandName.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		String[] args = sArgs.split("\\s");
		if (sArgs.length() < 1)
			return false;
		ChatChannel chan = ChannelManager.getChannel(args[0]);

		if (chan == null) {
			PluginUtil.send(sender, "&cChannel not found.");
			return true;
		}

		chan.name = args[1];

		PluginUtil.send(sender, "Channel name changed.");
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));

		return true;
	}
}
