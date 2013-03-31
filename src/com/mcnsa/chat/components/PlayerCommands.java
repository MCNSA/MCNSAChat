package com.mcnsa.chat.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.utils.StringUtils;

import com.mcnsa.chat.annotations.Command;
import com.mcnsa.chat.annotations.ComponentInfo;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.client.packets.ChannelUpdatePacket;
import com.mcnsa.chat.client.packets.PlayerChatPacket;
import com.mcnsa.chat.client.packets.PlayerPMPacket;
import com.mcnsa.chat.client.packets.PlayerUpdatePacket;
import com.mcnsa.chat.exceptions.ChatCommandException;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.managers.ChannelManager;
import com.mcnsa.chat.managers.MutelistManager;
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
	@Command(command = "list",
			description = "Displays online players",
			arguments = {}
			)
	public static boolean listPlayers(CommandSender player) throws ChatCommandException{
		PluginUtil.send(player.getName(), PluginUtil.getPlayerList());
		return true;
	}
	@Command(command = "clisten",
			arguments = {"Channel"},
			description = "Listens to a channel",
			permissions = {"listen"})
	public static boolean ListenChannel(CommandSender player, String channel) throws ChatCommandException{
		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		ChatChannel chan = ChannelManager.getChannel(channel);
		String read_perm = chan == null ? "" : chan.read_permission;
		if (!read_perm.equals("") && !MCNSAChat.permissions.has(Bukkit.getPlayer(player.getName()), "mcnsachat.read." + read_perm)) {
			Logger.log(player.getName() + " attempted to read channel " + channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}

		if (cp.listening.contains(channel.toLowerCase())) {
			cp.listening.remove(channel.toLowerCase());
			PluginUtil.send(player.getName(), "You are now no longer listening to channel " + chan.color + chan.name);
			return true;
		}
		cp.listening.add(channel.toLowerCase());

		if (chan == null) {
			chan = new ChatChannel(channel);
			ChannelManager.channels.add(chan);
			if (MCNSAChat.thread != null)
				MCNSAChat.thread.write(new ChannelUpdatePacket(chan));
		}

		// welcome them
		PluginUtil.sendLater(cp.name, "You are now listening to channel " + chan.color + chan.name + "&f!");
		ArrayList<String> names = new ArrayList<String>();
		for (ChatPlayer p : PlayerManager.getPlayersInChannel(chan.name))
			names.add(p.name);
		PluginUtil.sendLater(cp.name, "Players here: " + PluginUtil.formatPlayerList(names.toArray(new String[0])));

		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerUpdatePacket(cp));

		return true;
	}
	@Command(command = "me",
			arguments = {"action"},
			description = "Emotes your message",
			permissions = {"me"})
	public static boolean me(CommandSender player, String action) throws ChatCommandException{
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		String write_perm = ChannelManager.getChannel(p.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(Bukkit.getPlayer(player.getName()), "mcnsachat.write." + write_perm)) {
			Logger.log(player.getName() + " attempted to write to channel " + p.channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		if(p.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are not allowed to speak right now.");
			return true;
		}
		MCNSAChat.chat.action(p, action, null);
		// tell *everybody!*
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerChatPacket(p, action, null, PlayerChatPacket.Type.ACTION));
		return true;
	}
	@Command(command = "csearch",
			description = "Find what channel a player is in",
			arguments = {"Player"},
			permissions = {"search"}
			)
	public static boolean findPlayers(CommandSender player, String findPlayer) throws ChatCommandException{
		Player bukkitPlayer = Bukkit.getPlayer(findPlayer);
		if(bukkitPlayer == null) {
			PluginUtil.send(player.getName(), "&cPlayer not found.");
			return true;
		}
		ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), MCNSAChat.name);
		ChatChannel chan = ChannelManager.getChannel(p.channel);
		PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name) + "&f is in channel " + chan.color + chan.name);

		return true;
	}
	@Command(command = "ranks",
			description = "Display server ranks",
			arguments = {},
			permissions = {"ranks"}
			)
	public static boolean ranks(CommandSender player) throws ChatCommandException{
		PermissionGroup[] groups = MCNSAChat.permissions.getGroups();
		Arrays.sort(groups, new Comparator<PermissionGroup>() {
			public int compare(PermissionGroup a, PermissionGroup b) {
				int ra = a.getOptionInteger("rank", "", 9999);
				int rb = b.getOptionInteger("rank", "", 9999);
				return (ra < rb ? 1 : (ra > rb ? -1 : 0));
			}
		});

		String ranks = "&fRanks in order of least to greatest: ";
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getOptionInteger("rank", "", 9999) != 9999) 
				ranks += groups[i].getPrefix() + groups[i].getName() + "&f" + (i == groups.length - 1 ? "" : ", ");
		PluginUtil.send(player.getName(), ranks);
		return true;
	}
	@Command(command = "r",
			description = "Reply to message",
			arguments = {"message"},
			permissions = {"msg"}
			)
	public static boolean reply(CommandSender player, String... msg) throws ChatCommandException{
		ChatPlayer from = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);

		String to = from.lastPM;
		String message = StringUtils.implode(msg, " ");

		if (to == null || to.length() < 1) {
			PluginUtil.send(from.name, "&cThere is nobody to reply to!");
			return true;
		}
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByName(to);
		if (tos.size() == 0) {
			PluginUtil.send(from.name, "&cPlayer not found");
			return true;
		}

		MCNSAChat.chat.pm_send(from, to, message);
		if (Bukkit.getPlayerExact(to) != null)
			MCNSAChat.chat.pm_receive(from, to, message);
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerPMPacket(from, to, message));

		return true;
	}
	@Command(
		command = "mute",
		arguments = {"Player"},
		description = "Stops display of [player]'s chat and msgs"
		)
	public static boolean mute (CommandSender player, String mutePlayer) throws ChatCommandException{
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByFuzzyName(mutePlayer);
		if(tos.size() == 0) {
			PluginUtil.send(player.getName(), "&cPlayer not found");
			return true;
		}
		ArrayList<String> uniques = new ArrayList<String>();
		for(ChatPlayer tooo : tos) {
			if(!uniques.contains(tooo.name))
				uniques.add(tooo.name);
		}
		if(uniques.size() > 1) {
			String matches = "";
			for(String match : uniques)
				matches += match + " ";
			PluginUtil.send(player.getName(), matches);
			return true;
		}
		String playeMute = uniques.get(0);
	
		//This is where we set if the player is muted or not
		if (MutelistManager.mutelist.containsKey(player.getName()+"."+playeMute)) {
			//Player is already muted so lets remove
			MutelistManager.mutelist.remove(player.getName()+"."+playeMute);
			PluginUtil.send(player.getName(), PluginUtil.formatUser(playeMute)+ " has been unmuted");
		}
		else {
			//Player is not already muted, so lets mute
			MutelistManager.mutelist.put(player.getName()+"."+playeMute, "111");
			PluginUtil.send(player.getName(), PluginUtil.formatUser(playeMute)+ " has been muted");
		}	
		return true;
	}
	@Command(
		command = "msg",
		arguments = {"player", "message"},
		permissions = {"msg"},
		description = "Sends a message to [player]"
		)
	public static boolean msg(CommandSender player, String msgPlayer, String... msg) throws ChatCommandException{
		//Get the player thats sending
		ChatPlayer from = PlayerManager.getPlayer(player.getName(), MCNSAChat.name);
		//Get the player your sending to
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByFuzzyName(msgPlayer);
		if(tos.size() == 0) {
			PluginUtil.send(from.name, "&cPlayer not found");
			return true;
		}
		ArrayList<String> uniques = new ArrayList<String>();
		for(ChatPlayer tooo : tos) {
			if(!uniques.contains(tooo.name))
				uniques.add(tooo.name);
		}
		if(uniques.size() > 1) {
			String matches = "";
			for(String match : uniques)
				matches += match + " ";
			PluginUtil.send(from.name, matches);
			return true;
		}
		String to = uniques.get(0);
		
		//Build the message string
		String message = StringUtils.implode(msg, " ");
		
		//send the message
		MCNSAChat.chat.pm_send(from, to, message);
		if(Bukkit.getPlayerExact(to) != null)
			MCNSAChat.chat.pm_receive(from, to, message);
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerPMPacket(from, to, message));
	
		return true;
	}
}
