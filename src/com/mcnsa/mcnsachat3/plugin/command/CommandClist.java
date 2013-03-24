package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "clist", permission = "list", description = "lists available occupied channels")
public class CommandClist implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandClist(MCNSAChat3 plugin) {
		CommandClist.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		String chans = "";
		for(ChatChannel chan : ChannelManager.channels) {
			String perm = chan.read_permission;
			boolean hasPerm = perm.equals("") || MCNSAChat3.hasPermission(sender, "mcnsachat3.read." + perm);
			boolean chanOccupied = PlayerManager.getPlayersInChannel(chan.name).size() > 0 || chan.modes.contains(ChatChannel.Mode.PERSIST);
			if(hasPerm && chanOccupied) 
				chans += chan.color + chan.name + " ";
		}
		PluginUtil.send(sender, "Channels: " + chans);
		return true;
	}
}
