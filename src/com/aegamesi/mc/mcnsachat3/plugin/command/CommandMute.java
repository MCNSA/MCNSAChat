package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.HashMap;
import java.util.Map;

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
	Map<String, String> mutelist = new HashMap<String, String>();
	
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
		String playeMute = Bukkit.getPlayer(sArgs).getName();
		ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
		//This is where we set if the player is muted or not
		this.mutelist = plugin.muteManager.mutelist;
		if (mutelist.containsKey(player.getName()+"."+playeMute)) {
			//Player is already muted so lets remove
			mutelist.remove(player.getName()+"."+playeMute);
			PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name)+ " has been unmuted");
		}
		else {
			//Player is not already muted, so lets mute
			mutelist.put(player.getName()+"."+playeMute, "111");
			PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name)+ " has been muted");
		}
		
		//save the mutelist
		plugin.muteManager.update(mutelist);
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(p));
		return true;
	}
}
