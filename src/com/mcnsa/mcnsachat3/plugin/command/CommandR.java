package com.mcnsa.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerPMPacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "r", permission = "msg", usage = "<message>", description = "replies to a private message", playerOnly = true)
public class CommandR implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandR(MCNSAChat3 plugin) {
		CommandR.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		if (sArgs.length() < 1) {
			return false;
		}
		
		Player player = (Player)sender;

		ChatPlayer from = PlayerManager.getPlayer(player.getName(), plugin.name);

		String to = from.lastPM;
		String message = sArgs;

		if (to == null || to.length() < 1) {
			PluginUtil.send(from.name, "&cThere is nobody to reply to!");
			return true;
		}
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByName(to);
		if (tos.size() == 0) {
			PluginUtil.send(from.name, "&cPlayer not found");
			return true;
		}

		plugin.chat.pm_send(from, to, message);
		if (Bukkit.getPlayerExact(to) != null)
			plugin.chat.pm_receive(from, to, message);
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerPMPacket(from, to, message));

		return true;
	}
}
