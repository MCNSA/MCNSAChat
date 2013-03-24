package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.packets.ChannelUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "ccolor", permission = "color", usage = "<channel> <color>", description = "changes the color of a channel")
public class CommandColor implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandColor(MCNSAChat3 plugin) {
		CommandColor.plugin = plugin;
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

		chan.color = "&" + args[1];

		PluginUtil.send(sender, "Channel color changed.");
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));

		return true;
	}
}
