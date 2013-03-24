package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "c", permission = "channel", usage = "<channel>", description = "switches to a channel.", playerOnly = true)
public class CommandChannel implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandChannel(MCNSAChat3 plugin) {
		CommandChannel.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if(sArgs.length() < 1)
			return false;
		
		Player player = (Player)sender;
		
		String chanName = sArgs.replaceAll("[^A-Za-z0-9]", "");
		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), plugin.name);
		if(cp.modes.contains(ChatPlayer.Mode.LOCKED)) {
			PluginUtil.send(sender, "You have been locked in your channel and may not change channels.");
			return true;
		}

		String read_perm = ChannelManager.getChannel(chanName) == null ? "" : ChannelManager.getChannel(chanName).read_permission;
		if (!read_perm.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.read." + read_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to read channel " + sArgs + " without permission!");
			PluginUtil.send(sender, "&cYou don't have permission to do that!");
			return true;
		}
		
		cp.changeChannels(chanName);
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(cp));
		
		return true;
	}
}
