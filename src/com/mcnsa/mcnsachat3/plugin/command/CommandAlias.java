package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.packets.ChannelUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "calias", permission = "alias", usage = "<channel> <alias>", description = "changes the alias of a channel")
public class CommandAlias implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandAlias(MCNSAChat3 plugin) {
		CommandAlias.plugin = plugin;
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

		if (!chan.alias.equals(""))
			plugin.command.aliases.remove(chan.alias);
		if (args[1].equals("null"))
			chan.alias = "";
		else
			chan.alias = args[1];
		plugin.command.aliases.put(chan.alias, chan.name);

		PluginUtil.send(sender, "Channel alias changed! Now: '" + chan.alias + "'");
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));
		return true;
	}
}
