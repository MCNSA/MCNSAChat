package com.mcnsa.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.ChannelUpdatePacket;
import com.mcnsa.mcnsachat3.packets.PlayerUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "clisten", permission = "listen", usage = "<channel>", description = "toggles listening to a channel.", playerOnly = true)
public class CommandListen implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandListen(MCNSAChat3 plugin) {
		CommandListen.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if(sArgs.length() < 1)
			return false;

		Player player = (Player)sender;
		
		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), plugin.name);
		ChatChannel chan = ChannelManager.getChannel(sArgs);
		String read_perm = chan == null ? "" : chan.read_permission;
		if (!read_perm.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.read." + read_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to read channel " + sArgs + " without permission!");
			PluginUtil.send(sender, "&cYou don't have permission to do that!");
			return true;
		}
		
		if (cp.listening.contains(sArgs.toLowerCase())) {
			cp.listening.remove(sArgs.toLowerCase());
			PluginUtil.send(sender, "You are now no longer listening to channel " + chan.color + chan.name);
			return true;
		}
		cp.listening.add(sArgs.toLowerCase());

		if (chan == null) {
			chan = new ChatChannel(sArgs);
			ChannelManager.channels.add(chan);
			if (MCNSAChat3.thread != null)
				MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));
		}

		// welcome them
		PluginUtil.sendLater(sender, "You are now listening to channel " + chan.color + chan.name + "&f!");
		ArrayList<String> names = new ArrayList<String>();
		for (ChatPlayer p : PlayerManager.getPlayersInChannel(chan.name))
			names.add(p.name);
		PluginUtil.sendLater(sender, "Players here: " + PluginUtil.formatPlayerList(names.toArray(new String[0])));
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(cp));
		
		return true;
	}
}
