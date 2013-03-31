package com.mcnsa.chat.components;

import java.io.IOException;
import java.util.Date;

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
import com.mcnsa.chat.managers.TimeoutManager;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;
import com.mcnsa.chat.utilities.StringUtils;

@ComponentInfo(friendlyName = "Admin",
description = "Admin commands",
permsSettingsPrefix = "mcnsachat.admin")
public class AdminCommands {
	
	@Command(command = "cmove",
			arguments = {"Player", "Channel"},
			description = "Move player to a channel",
			permissions = {"move"})
	public static boolean Cmove(CommandSender sender, String targetPlayer, String channel) throws ChatCommandException {
		Logger.log(targetPlayer+".."+channel);
		Player[] targets = new Player[1];
		if (targetPlayer.equals("*")) {
			targets = Bukkit.getServer().getOnlinePlayers();
		} else {
			if ((targets[0] = Bukkit.getServer().getPlayer(targetPlayer)) == null) {
				PluginUtil.send(sender.getName(), "&cPlayer not found");
				return true;
			}
		}

		for (Player target : targets) {
			ChatPlayer cp = PlayerManager.getPlayer(target.getName(), MCNSAChat.name);
			cp.changeChannels(channel);
			Logger.log(sender.getName()+ " Moved " + PluginUtil.formatUser(cp.name) + " &rto " + ChannelManager.getChannel(cp.channel).color + ChannelManager.getChannel(cp.channel).name);
			if (MCNSAChat.thread != null)
				MCNSAChat.thread.write(new PlayerUpdatePacket(cp));
		}
		return true;
	}
	@Command(command = "creconnect",
			description = "Disconnect from the chatserver",
			permissions = {"disconnect"})
	public static boolean reconnect(CommandSender sender) {
		if (MCNSAChat.thread != null) {
			try {
				MCNSAChat.thread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PluginUtil.send(sender.getName(), "Broke connection");
		return true;
	}
	@Command(command = "clock",
			arguments = {"Player"},
			description = "Lock player from changing channels",
			permissions = {"lock"})
	public static boolean lock(CommandSender sender, String targetPlayer) {
		Player bukkitPlayer = Bukkit.getPlayer(targetPlayer);
		if (bukkitPlayer == null) {
			PluginUtil.send(sender.getName(), "Player not found");
			return true;
		}
		else {
			ChatPlayer chatPlayer = PlayerManager.getPlayer(bukkitPlayer.getName(), MCNSAChat.name);
			
			//check the current mode, if already locked, unlock
			if (chatPlayer.modes.contains(ChatPlayer.Mode.LOCKED)) {
				//Player is already locked. Unlock
				chatPlayer.modes.remove(ChatPlayer.Mode.LOCKED);
				PluginUtil.send(chatPlayer.name, "You are no longer locked in your channel");
				//Notify the person who locked them
				PluginUtil.send(sender.getName(), "Unlocked "+chatPlayer.name);
				
				//log to console
				Logger.log(chatPlayer.name+" Unlocked by "+sender.getName());
			}
			else {
				//Player is unlocked. Lock them in the channel
				chatPlayer.modes.add(ChatPlayer.Mode.LOCKED);
				PluginUtil.send(chatPlayer.name, "You are now locked in your channel");
				//Notify the person who locked them
				PluginUtil.send(sender.getName(), "Locked "+chatPlayer.name);
				
				//Log to console
				Logger.log(chatPlayer.name+" Locked by "+sender.getName());
			}
			
			
			
			//Update other servers
			if (MCNSAChat.thread != null) {
				MCNSAChat.thread.write(new PlayerUpdatePacket(chatPlayer));
			}
		}
		return true;
	}
	@Command(command = "cto",
			arguments = {"Player", "Time", "Reason"},
			description = "Stops a player from chatting",
			permissions = {"timeout"})
	public static boolean timeout(CommandSender sender, String player, String time, String... rawReason) {
		//Get the player
		ChatPlayer target = PlayerManager.getPlayer(Bukkit.getPlayer(player).getName(), MCNSAChat.name);
		
		//Build the reason string
		String reason = StringUtils.implode(" ", rawReason);
		//Check if the player is already in timeout
		if (TimeoutManager.timeouts.containsKey(target.name)) {
			//Player is already in timeout.
			PluginUtil.send(sender.getName(), "&c"+target.name+" is already in timeout");
			return true;
		}
		
		////sort out when we need to get them out of timeout
		int timer = Integer.parseInt(time);
		timer = (timer * 60)*1000;
		long currentTime = new Date().getTime();
		long timeOut = currentTime + timer;
		
		//Add the player to the timeout list
		TimeoutManager.timeouts.put(target.name, timeOut);
		
		//add the mute mode which prevents chatting
		target.modes.add(ChatPlayer.Mode.MUTE);
		
		//Notify the target.
		PluginUtil.send(target.name, "&4You have been put in timeout for &6"+time+"&4 mins. Reason: &6"+reason);
		
		//Notify everyone else
		PluginUtil.send("&4"+target.name+" &6Has been put in timeout for: &4"+reason);
				
		return true;
	}
	@Command(command = "cto",
			arguments = {"Player"},
			description = "Removes player from timeout",
			permissions = {"lock"})
	public static boolean unTimeout(CommandSender sender, String player) {
		//Get the player
		ChatPlayer target = PlayerManager.getPlayer(Bukkit.getPlayer(player).getName(), MCNSAChat.name);
		
		//Check if the player is already in timeout
		if (TimeoutManager.timeouts.containsKey(target.name)) {
			//Player is already in timeout.
			TimeoutManager.timeouts.remove(target.name);
			
			//Notify everyone that player is out of timeout
			PluginUtil.send("&4"+target.name+" &6has been removed from timeout");
			return true;
		}
		PluginUtil.send(sender.getName(), "&4"+target.name+" &6is not in timeout");
		return true;
	}
	@Command(command = "cto",
			arguments = {},
			description = "Removes player from timeout",
			permissions = {"lock"})
	public static boolean timeoutList(CommandSender sender) {
		
		if (sender.getName().contains("CONSOLE")) {
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color("&6Players in timeout"));
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color("&4------------------"));
		}
		else {
			PluginUtil.send(sender.getName(), "&6Players in timeout");
			PluginUtil.send(sender.getName(), "&4------------------");
		}
		int count = 0;
		for (String key: TimeoutManager.timeouts.keySet()) {
			//Check if its the console sending
			if (sender.getName().contains("CONSOLE")) {
				Bukkit.getConsoleSender().sendMessage(PluginUtil.color("&6"+key+": "+ new Date(TimeoutManager.timeouts.get(key)).toString()));
			}
			else {
				PluginUtil.send(sender.getName(), "&6"+key+": "+ new Date(TimeoutManager.timeouts.get(key)).toString());
			}
			count++;
		}
		if (count == 0){
			if (sender.getName().contains("CONSOLE")) {
				Bukkit.getConsoleSender().sendMessage(PluginUtil.color("&6None"));
			}
			else {
				PluginUtil.send(sender.getName(), "&6None");
			}
		}
		return true;
	}

}
