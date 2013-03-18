package com.aegamesi.mc.mcnsachat3.plugin.command.fun;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerChatPacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.command.Command;

@Command.CommandInfo(alias = "mab", permission = "fun", usage = "", description = "")
public class CommandMab implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMab(MCNSAChat3 plugin) {
		CommandMab.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), plugin.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.write." + write_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		if (p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are not allowed to speak right now.");
			return true;
		}

		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerChatPacket(p, "Remember, I come pre lubed ;)", null, PlayerChatPacket.Type.CHAT));

		plugin.chat.chat(p, "Remember, I come pre lubed ;)", null);
		return true;
	}
}