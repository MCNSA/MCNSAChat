package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "cmove", permission = "move", usage = "<player> <channel>", description = "moves a player to a channel.")
public class CommandMove implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMove(MCNSAChat3 plugin) {
		CommandMove.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if (sArgs.length() < 1)
			return false;

		String[] args = sArgs.split("\\s");
		if (args.length != 2) {
			return false;
		}
		String channel = args[1];

		Player[] targets = new Player[1];
		if (args[0].equals("*")) {
			targets = plugin.getServer().getOnlinePlayers();
		} else {
			if ((targets[0] = plugin.getServer().getPlayer(args[0])) == null) {
				PluginUtil.send(sender, "&cPlayer not found");
				return true;
			}
		}

		for (Player target : targets) {
			ChatPlayer cp = PlayerManager.getPlayer(target.getName(), plugin.name);
			cp.changeChannels(channel);
			PluginUtil.send(sender, "Moved " + PluginUtil.formatUser(cp.name) + " &rto " + ChannelManager.getChannel(cp.channel).color + ChannelManager.getChannel(cp.channel).name);
			if (MCNSAChat3.thread != null)
				MCNSAChat3.thread.write(new PlayerUpdatePacket(cp));
		}
		return true;
	}
}
