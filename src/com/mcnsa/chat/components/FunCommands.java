package com.mcnsa.chat.components;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.mcnsa.chat.annotations.Command;
import com.mcnsa.chat.annotations.ComponentInfo;
import com.mcnsa.chat.chat.ChatChannel;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.client.packets.PlayerChatPacket;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.managers.ChannelManager;
import com.mcnsa.chat.managers.PlayerManager;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;


@ComponentInfo(friendlyName = "Fun",
description = "Fun commands",
permsSettingsPrefix = "mcnsachat.fun")
public class FunCommands {
	@Command(
			command = "dicks",
			description = "A message of love",
			permissions = {"dicks"}
			)
	public static boolean dicks(CommandSender player){
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(Bukkit.getPlayer(player.getName()), "mcnsachat3.write." + write_perm)) {
			Logger.log(player.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		if (p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are not allowed to speak right now.");
			return true;
		}

		String message = MCNSAChat.plugin.getConfig().getString("command-dicks");
		message = message.replace("%player%", p.name);
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerChatPacket(p, message, null, PlayerChatPacket.Type.CHAT));

		MCNSAChat.chat.chat(p, message, null);
		return true;
	}
	@Command(
			command = "mab",
			description = "A message of love",
			permissions = {"mab"}
			)
	public static boolean mab(CommandSender player){
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(Bukkit.getPlayer(player.getName()), "mcnsachat3.write." + write_perm)) {
			Logger.log(player.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		if (p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are not allowed to speak right now.");
			return true;
		}

		String message = MCNSAChat.plugin.getConfig().getString("command-mab");
		message = message.replace("%player%", p.name);
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerChatPacket(p, message, null, PlayerChatPacket.Type.CHAT));

		MCNSAChat.chat.chat(p, message, null);
		return true;
	}
	@Command(
			command = "pong",
			description = "A message of love",
			permissions = {"pong"}
			)
	public static boolean pong(CommandSender player){
		PluginUtil.send(player.getName(), "I hear " + player.getName() + " likes cute asian boys.");
		return true;
	}
	@Command(
			command = "rand",
			description = "roll a random number",
			arguments = {"min", "max"},
			permissions = {"rand"}
			)
	public static boolean rand(CommandSender player, String min, String max){
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(Bukkit.getPlayer(player.getName()), "mcnsachat3.write." + write_perm)) {
			Logger.log(player.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		if (p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are not allowed to speak right now.");
			return true;
		}

			//Base variables
			int start = 0;
			int end = 20;
			try {
				start = Integer.parseInt(min);
				end = Integer.parseInt(max);
			}
			catch(Exception e){
				
			}
			Random rn = new Random();
			int range = end - start + 1;
			int randomNumber =  rn.nextInt(range) + start;  
		//Send to everyone
		PluginUtil.send("&6"+player.getName()+" &frolled the number &6"+randomNumber);
		return true;
	}
}
