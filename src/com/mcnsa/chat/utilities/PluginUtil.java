package com.mcnsa.chat.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.managers.MutelistManager;

public class PluginUtil {
	public static MCNSAChat plugin = null;

	public static String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String raveColor(String str) {
		Random r = new Random();
		String newStr = "";
		String colors = "123456789abcde";
		for (int i = 0; i < str.length(); i++)
			newStr += "&" + colors.charAt(r.nextInt(colors.length())) + str.charAt(i);
		return newStr;
	}

	public static String stripColor(String str) {
		int count = StringUtils.countMatches(str, "&");
		for (int i=0; i < count; i++) {
			str = ChatColor.stripColor(color(str));
		}
		//return ChatColor.stripColor(color(str));
		return str;
		
	}

	public static String formatUser(String user) {
		return MCNSAChat.permissions.getUser(user).getPrefix() + user;
	}

	public static String formatRank(String user) {
		return color(MCNSAChat.permissions.getUser(user).getPrefix() + MCNSAChat.permissions.getUser(user).getSuffix());
	}

	public static void send(String who, String message) {
		if (message.length() <= 0)
			return;
		Player player = Bukkit.getPlayerExact(who);
		if (player != null)
			player.sendMessage(color(message));
	}

	public static void send(String message) {
		if (message.length() <= 0)
			return;
		Bukkit.broadcastMessage(color(message));
	}

	public static void sendLater(final String who, final String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				send(who, message);
			}
		}, 0L);
	}
	public static void sendLaterBlock(final String who, final String message, final String sender) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (!(MutelistManager.mutelist.containsKey(who+"."+sender))){
					send(who, message);
				}
			}
		}, 0L);
	}

	public static String getPlayerList() {
		Player[] list = Bukkit.getServer().getOnlinePlayers();
		ArrayList<String> names = new ArrayList<String>();
		for (Player player : list)
			names.add(player.getName());
		return "&7Online (" + list.length + "/" + Bukkit.getServer().getMaxPlayers() + "): " + formatPlayerList(names.toArray(new String[0]));
	}

	public static String formatPlayerList(String[] list) {
		Arrays.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int ra = MCNSAChat.permissions.getUser(a).getOptionInteger("rank", "", 9999);
				int rb = MCNSAChat.permissions.getUser(b).getOptionInteger("rank", "", 9999);
				return (ra < rb ? 1 : (ra > rb ? -1 : 0));
			}
		});
		String out = "";
		for (int i = 0; i < list.length; i++)
			out += MCNSAChat.permissions.getUser(list[i]).getPrefix() + list[i] + (i < list.length - 1 ? "&7, " : "");
		return out;
	}

	public static void sendLater(final String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				send(message);
			}
		}, 0L);
	}
}
