package com.mcnsa.mcnsachat3.plugin.command;

import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.managers.TimeoutsManager;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "cto", permission = "timeout", usage = "<player> <time> <reason>", description = "Puts a player in timeout")
public class CommandTimeout implements Command {
	public static MCNSAChat3 plugin = null;
	
	public CommandTimeout(MCNSAChat3 plugin) {
		CommandTimeout.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
			
		String[] args = sArgs.split(" ");
		
		if (args.length > 0 && args[0].equalsIgnoreCase("")) {
			//List players in timeout
			PluginUtil.send(player.getName(), "&6Players in timeout");
			PluginUtil.send(player.getName(), "&4------------------");
			int count = 0;
			for (String key: TimeoutsManager.timeouts.keySet()) {
				PluginUtil.send(player.getName(), "&6"+key+": "+ new Date(TimeoutsManager.timeouts.get(key)).toString());
				count++;
			}
			if (count == 0){
				PluginUtil.send(player.getName(), "&6None");
			}
		}
		else if ((args.length < 2) && args.length >= 1) {
			//Trying to remove a player from timeout
			if (!(Bukkit.getPlayer(args[0]) != null)) {
				//Player doesn't exist
				PluginUtil.send(player.getName(), "&4Could not get player");
			}
			else {
				String playerTimeout = Bukkit.getPlayer(args[0]).getName();
				
				if (TimeoutsManager.timeouts.get(playerTimeout) != null){
					TimeoutsManager.timeouts.remove(args[0]);
					Player bukkitPlayer = Bukkit.getPlayer(args[0]);
					ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
					p.modes.remove(ChatPlayer.Mode.MUTE);
					PluginUtil.send(p.name, "You are no longer in timeout.");
					PluginUtil.send(player.getName(), "&6"+playerTimeout+" Removed from timeout");
					PluginUtil.send("&6"+playerTimeout+" &4has been removed from timeout");
				}
				else {
					PluginUtil.send(player.getName(), "&4"+playerTimeout+" is not in timeout");
				}
			}
			
		}
		else if (args.length >= 2) {
			//Trying to put a player in timeout
			if (!(Bukkit.getPlayer(args[0]) != null)) {
				//Player doesn't exist
				PluginUtil.send(player.getName(), "&4Could not get player");
				return true;
			}
			
			//Build the reason string
			StringBuffer string = new StringBuffer();
			for (int i=2; i < args.length; i++) {
				string.append(args[i]+" ");
			}
			String msg = string.toString();
			//Is the Reason string empty?
			if (msg.length() < 3) {
				//String is empty. Use a default
				msg = "Annoying a mod too much";
			}
			//sort out when we need to get them out of timeout
			int time = Integer.parseInt(args[1]);
			time = (time * 60)*1000;
			long currentTime = new Date().getTime();
			long timeOut = currentTime + time;
			
			//get the player
			String playerTimeout = Bukkit.getPlayer(args[0]).getName();
			TimeoutsManager.timeouts.put(playerTimeout, timeOut);
			Player bukkitPlayer = Bukkit.getPlayer(args[0]);
			ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
			
			//Tell the player that they have been put in timeout
			PluginUtil.send(p.name, "&4You have been put in timeout for: &6"+ args[1] +"&4min. Reason:&6"+msg);
			//add mute mode
			p.modes.add(ChatPlayer.Mode.MUTE);
			
			//Tell everyone else
			PluginUtil.send("&4"+playerTimeout+" &6Has been put in timeout for &4"+args[1]+"min &6Reason: "+msg);
			}	
		
		return true;
	}
}
