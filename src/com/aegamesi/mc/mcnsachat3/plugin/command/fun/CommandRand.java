package com.aegamesi.mc.mcnsachat3.plugin.command.fun;

import java.util.Random;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.command.Command;

@Command.CommandInfo(alias = "rand", permission = "fun", usage = "/rand <start> <end> - Random number between <Start> and <end>", description = "")
public class CommandRand implements Command {
	private static final long aEnd = 0;
	public static MCNSAChat3 plugin = null;

	public CommandRand(MCNSAChat3 plugin) {
		CommandRand.plugin = plugin;
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
		
		//Split arguments and assign to variables	
			String[] args = sArgs.split(" ");
			//Base variables
			int start = 0;
			int end = 20;
			//Check if the start/end have been specified, if so assign
			if (args.length == 1) {
				end = Integer.parseInt(args[0]);
			}
			if (args.length == 2) {
				start = Integer.parseInt(args[0]);
				end = Integer.parseInt(args[1]);
				//Check if start greater than end
				if (start > end) {
					//Reverse them
					end	= Integer.parseInt(args[0]);
					start = Integer.parseInt(args[1]);
				}
			}
			Random rn = new Random();
			int range = end - start + 1;
			int randomNumber =  rn.nextInt(range) + start;  
		//Send to everyone
		PluginUtil.send("&6"+player.getName()+" &frolled the number &6"+randomNumber);
		return true;
	}
}
