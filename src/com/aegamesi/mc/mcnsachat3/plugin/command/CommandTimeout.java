package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.Persistence;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.SLAPI;

@Command.CommandInfo(alias = "timeout", permission = "timeout", usage = "<player> <time> <reason>", description = "Puts a player in timeout")
public class CommandTimeout implements Command {
	public static MCNSAChat3 plugin = null;
	
	public CommandTimeout(MCNSAChat3 plugin) {
		CommandTimeout.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		//Initilise hashmap
		HashMap<String, Long > timeouts = new HashMap<String, Long>();
		//Try loading the hashmap
		try{ timeouts = SLAPI.load("plugins/MCNSAChat3/timeout.bin"); }
		catch(Exception e){ plugin.getLogger().warning("Error loading timeout hashmap. "+e.getMessage()); }
		
		String[] args = sArgs.split(" ");
		
		if (args.length > 0 && args[0].equalsIgnoreCase("")) {
			//List players in timeout
			PluginUtil.send(player.getName(), "&6Players in timeout");
			PluginUtil.send(player.getName(), "&4------------------");
			int count = 0;
			for (String key: timeouts.keySet()) {
				PluginUtil.send(player.getName(), "&6"+key+": "+ new Date(timeouts.get(key)).toString());
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
				if (timeouts.get(playerTimeout) != null){
					timeouts.remove(args[0]);
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
		else if (args.length > 2) {
			//Trying to put a player in timeout
			if (!(Bukkit.getPlayer(args[0]) != null)) {
				//Player doesn't exist
				PluginUtil.send(player.getName(), "&4Could not get player");
			}
			else {
				StringBuffer string = new StringBuffer();
				for (int i=2; i < args.length; i++) {
					string.append(args[i]+" ");
				}	
				int time = Integer.parseInt(args[1]);
				time = (time * 60)*1000;
				long currentTime = new Date().getTime();
				long timeOut = currentTime + time;
				String playerTimeout = Bukkit.getPlayer(args[0]).getName();
				timeouts.put(playerTimeout, timeOut);
				Player bukkitPlayer = Bukkit.getPlayer(args[0]);
				ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
				PluginUtil.send(p.name, "&4You have been put in timeout for: &6"+ args[1] +"&4min. Reason:&6"+string);
				p.modes.add(ChatPlayer.Mode.MUTE);
				PluginUtil.send(player.getName(), "&4"+playerTimeout+" &6Has been put in timeout for &4"+args[1]+"min &6Reason: "+string.toString());
			}
		}		
		
		//Save the hashmap
		try { SLAPI.save(timeouts, "plugins/MCNSAChat3/timeout.bin");}
		catch (Exception e) { plugin.getLogger().warning("Could not save timeouts file "+e.getMessage()); }
		return true;
	}
}
