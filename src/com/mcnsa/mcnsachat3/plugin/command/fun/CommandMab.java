package com.mcnsa.mcnsachat3.plugin.command.fun;

import org.bukkit.command.CommandSender;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerChatPacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;
import com.mcnsa.mcnsachat3.plugin.command.Command;

@Command.CommandInfo(alias = "mab", permission = "fun", usage = "", description = "")
public class CommandMab implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMab(MCNSAChat3 plugin) {
		CommandMab.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		ChatPlayer p = PlayerManager.getPlayer(sender.getName(), plugin.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat3.hasPermission(sender, "mcnsachat3.write." + write_perm)) {
			plugin.getLogger().info(sender.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(sender, "&cYou don't have permission to do that!");
			return true;
		}
		if (p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(sender, "You are not allowed to speak right now.");
			return true;
		}
		
		String message = plugin.getConfig().getString("command-mab");
		message = message.replace("%player%", p.name);
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerChatPacket(p, message, null, PlayerChatPacket.Type.CHAT));

		plugin.chat.chat(p, message, null);
		return true;
	}
}