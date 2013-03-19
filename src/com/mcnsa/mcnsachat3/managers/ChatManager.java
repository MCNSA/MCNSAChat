package com.mcnsa.mcnsachat3.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.MutelistManager;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;
import com.mcnsa.mcnsachat3.plugin.SLAPI;

public class ChatManager {
	public MCNSAChat3 plugin;

	public ChatManager(MCNSAChat3 plugin) {
		this.plugin = plugin;
	}

	public void pm_receive(ChatPlayer from, String to, String line) {
		if (MCNSAChat3.permissions.getUser(from.name).has("mcnsachat3.user.cancolor"))
			line = PluginUtil.color(line);
		else
			line = PluginUtil.stripColor(line);

		ChatPlayer cpto = PlayerManager.getPlayer(to, plugin.name);
		if (cpto == null)
			return;
		cpto.lastPM = from.name;

		String message = plugin.getConfig().getString("strings.pm_receive");
		message = message.replace("%message%", line);
		message = message.replace("%from%", from.name);
		message = message.replace("%to%", to);
		Map<String, String> mutelist = MutelistManager.load(plugin);
		if (!(mutelist.containsKey(to+"."+from.name))){
			PluginUtil.sendLater(to, message);
		}
		
	}

	public void pm_send(ChatPlayer from, String to, String line) {
		if (MCNSAChat3.permissions.getUser(from.name).has("mcnsachat3.user.cancolor"))
			line = PluginUtil.color(line);
		else
			line = PluginUtil.stripColor(line);

		from.lastPM = to;

		String message = plugin.getConfig().getString("strings.pm_send");
		message = message.replace("%message%", line);
		message = message.replace("%from%", from.name);
		message = message.replace("%to%", to);

		PluginUtil.sendLater(from.name, message);

		// and log it to the console
		System.out.println("[msg] " + from.name + " to " + to +": " + line);
	}

	public void chat(ChatPlayer player, String line, String channel) {
		ChatChannel chan = ChannelManager.getChannel(PlayerManager.getPlayer(player).channel);
		if (channel == null || channel.length() <= 0)
			channel = chan.name;
		else
			chan = ChannelManager.getChannel(channel);

		if (MCNSAChat3.permissions.getUser(player.name).has("mcnsachat3.user.cancolor") && !chan.modes.contains(ChatChannel.Mode.BORING))
			line = PluginUtil.color(line);
		else
			line = PluginUtil.stripColor(line);

		if (chan.modes.contains(ChatChannel.Mode.RANDOM))
			line = "&k" + line;
		if (chan.modes.contains(ChatChannel.Mode.LOUD))
			line = PluginUtil.color("&l" + line).toUpperCase();
		if (chan.modes.contains(ChatChannel.Mode.RAVE))
			line = PluginUtil.raveColor(line);

		String message = plugin.getConfig().getString("strings.message");
		message = message.replace("%server%", player.server);
		message = message.replace("%channel%", chan.color + chan.name);
		message = message.replace("%rank%", PluginUtil.formatRank(player.name));
		message = message.replace("%prefix%", MCNSAChat3.permissions.getUser(player.name).getPrefix());
		message = message.replace("%player%", player.name);
		message = message.replace("%message%", line);

		info(player, message, chan.name, !(player.server.equals(plugin.name)), player.name);
	}

	public void action(ChatPlayer player, String line, String channel) {
		ChatChannel chan = ChannelManager.getChannel(PlayerManager.getPlayer(player).channel);
		if (channel == null || channel.length() <= 0)
			channel = chan.name;
		else
			chan = ChannelManager.getChannel(channel);

		if (MCNSAChat3.permissions.getUser(player.name).has("mcnsachat3.user.cancolor") && !chan.modes.contains(ChatChannel.Mode.BORING))
			line = PluginUtil.color(line);
		else
			line = PluginUtil.stripColor(line);

		if (chan.modes.contains(ChatChannel.Mode.RANDOM))
			line = "&k" + line;
		if (chan.modes.contains(ChatChannel.Mode.LOUD))
			line = PluginUtil.color("&l" + line).toUpperCase();
		if (chan.modes.contains(ChatChannel.Mode.RAVE))
			line = PluginUtil.raveColor(line);

		String message = plugin.getConfig().getString("strings.action");
		message = message.replace("%server%", player.server);
		message = message.replace("%channel%", chan.color + chan.name);
		message = message.replace("%rank%", PluginUtil.formatRank(player.name));
		message = message.replace("%prefix%", MCNSAChat3.permissions.getUser(player.name).getPrefix());
		message = message.replace("%suffix%", MCNSAChat3.permissions.getUser(player.name).getSuffix());
		message = message.replace("%player%", player.name);
		message = message.replace("%message%", line);

		info(player, message, chan.name, !(player.server.equals(plugin.name)), player.name);
	}

	public void info(ChatPlayer player, String line, String channel, boolean net, String sender) {
		ChatChannel chan = ChannelManager.getChannel(channel);
		if (chan == null)
			return;
		ArrayList<ChatPlayer> players = PlayerManager.getPlayersListeningToChannel(chan.name);		
		for (ChatPlayer p : players) {
			boolean send = player == null;
			if (!send) {
				if (net)
					send = Bukkit.getPlayerExact(p.name) != null && !p.server.equals(player.server);
				else
					send = Bukkit.getPlayerExact(p.name) != null && p.server.equals(player.server);
			}
			
			if (send)
					PluginUtil.sendLaterBlock(p.name, line + "&r",sender);
		}
		if (plugin.getConfig().getString("console-listen-other-servers").startsWith("true")) {
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color(line));
		}
		if (line.contains("<&[a-z0-9]{1}"+plugin.name+"[a-z0-9]{1,30}&?f?>") && plugin.getConfig().getString("console-listen-other-servers").startsWith("false") && plugin.getConfig().getString("console-hide-chat").startsWith("false")) {
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color(line));
		}
	}
	
	public void info(ChatPlayer player, String line, String channel, boolean net) {
		ChatChannel chan = ChannelManager.getChannel(channel);
		if (chan == null)
			return;
		ArrayList<ChatPlayer> players = PlayerManager.getPlayersListeningToChannel(chan.name);
		for (ChatPlayer p : players) {
			boolean send = player == null;
			if (!send) {
				if (net)
					send = Bukkit.getPlayerExact(p.name) != null && !p.server.equals(player.server);
				else
					send = Bukkit.getPlayerExact(p.name) != null && p.server.equals(player.server);
			}
			if (send)
				PluginUtil.sendLater(p.name, line + "&r");
		}
		if (plugin.getConfig().getString("console-listen-other-servers").startsWith("true")) {
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color(line));
		}
		if (line.contains(plugin.name) && plugin.getConfig().getString("console-listen-other-servers").startsWith("false") && plugin.getConfig().getString("console-hide-chat").startsWith("false")) {
			Bukkit.getConsoleSender().sendMessage(PluginUtil.color(line));
		}
	}
}
