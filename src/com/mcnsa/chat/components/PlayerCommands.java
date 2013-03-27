package com.mcnsa.chat.components;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.chat.annotations.Command;
import com.mcnsa.chat.annotations.ComponentInfo;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.client.packets.PlayerUpdatePacket;
import com.mcnsa.chat.exceptions.ChatCommandException;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.managers.ChannelManager;
import com.mcnsa.chat.managers.PlayerManager;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;
import com.mcnsa.chat.chat.ChatChannel;

@ComponentInfo(friendlyName = "Player",
description = "Player commands",
permsSettingsPrefix = "mcnsachat.player")
public class PlayerCommands {
	@Command(command = "c",
			arguments = {"Channel"},
			description = "Move to a channel",
			permissions = {"move"})
	public static boolean channelChange(CommandSender sender, String channel) throws ChatCommandException{
		String chanName = channel.replaceAll("[^A-Za-z0-9]", "");
		ChatPlayer cp = PlayerManager.getPlayer(sender.getName(), MCNSAChat.name);
		if(cp.modes.contains(ChatPlayer.Mode.LOCKED)) {
			PluginUtil.send(cp.name, "You have been locked in your channel and may not change channels.");
			return true;
		}
		Player player = Bukkit.getPlayer(sender.getName());
		String read_perm = ChannelManager.getChannel(chanName) == null ? "" : ChannelManager.getChannel(chanName).read_permission;
		if (!read_perm.equals("") && !MCNSAChat.permissions.has(player, "mcnsachat.read." + read_perm)) {
			Logger.log(player.getName() + " attempted to read channel " + channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		String write_perm = ChannelManager.getChannel(chanName) == null ? "" : ChannelManager.getChannel(chanName).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(player, "mcnsachat.write." + write_perm)) {
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		cp.changeChannels(chanName);
		
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerUpdatePacket(cp));
		
		return true;
	}
	@Command(command = "clist",
			arguments = {},
			description = "Lists all channels",
			permissions = {"list"})
	public static boolean listChannels(CommandSender sender) {
		String chans = "";
		for(ChatChannel chan : ChannelManager.channels) {
			String perm = chan.read_permission;
			Player player = Bukkit.getPlayer(sender.getName());
			boolean hasPerm = perm.equals("") || MCNSAChat.permissions.has(player, "mcnsachat.read." + perm);
			boolean chanOccupied = PlayerManager.getPlayersInChannel(chan.name).size() > 0 || chan.modes.contains(ChatChannel.Mode.PERSIST);
			if(hasPerm && chanOccupied) 
				//Support to not show the broadcast channels
				if (chan.read_permission == chan.write_permission) {
					chans += chan.color + chan.name + "&f ";
				}
				
		}
		PluginUtil.send(sender.getName(), "Channels: " + chans);
		return true;
	}
}
