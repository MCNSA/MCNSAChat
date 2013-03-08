package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.SLAPI;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;

@Command.CommandInfo(alias = "cmute", permission = "mute", usage = "<player>", description = "Mute a player so you dont have to listen to them")
public class CommandMute implements Command {
	HashMap<String, String> mutelist = new HashMap<String, String>();
	public static MCNSAChat3 plugin = null;
	
	public CommandMute(MCNSAChat3 plugin) {
		 CommandMute.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		
		if(sArgs.length() < 1)
			return false;
	
		Player bukkitPlayer = Bukkit.getPlayer(sArgs);
		if(bukkitPlayer == null) {
			PluginUtil.send(player.getName(), "&cPlayer not found.");
			return true;
		}
		
		//Load the hashmap
		try{ mutelist = SLAPI.load("plugins/MCNSAChat3/mutelist.bin"); }
		catch(Exception e){ plugin.getLogger().warning("Error loading hash map. "+e.getMessage()); }
		
		int muted = 0;
		ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
		//This is where we set if the player is muted or not
		if (mutelist.containsKey(player.getName()+"."+sArgs)) {
			//Player is already muted so lets remove
			mutelist.remove(player.getName()+"."+sArgs);
			PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name)+ " has been unmuted");
		}
		else {
			//Player is not already muted, so lets mute
			mutelist.put(player.getName()+"."+sArgs, "111");
			PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name)+ " has been muted");
		}
		
		//save the mutelist
		try { SLAPI.save(mutelist, "plugins/MCNSAChat3/mutelist.bin");}
		catch (Exception e) { plugin.getLogger().warning("Could not save mutelist "+e.getMessage()); }
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(p));
		return true;
	}
}
