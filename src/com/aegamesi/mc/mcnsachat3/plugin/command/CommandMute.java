package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.MutelistManager;
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

		
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByFuzzyName(sArgs);
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
		this.mutelist = MutelistManager.load();
		if (this.mutelist.containsKey(player.getName()+"."+playeMute)) {
			//Player is already muted so lets remove
			this.mutelist.remove(player.getName()+"."+playeMute);
			PluginUtil.send(player.getName(), PluginUtil.formatUser(playeMute)+ " has been unmuted");
		}
		else {
			//Player is not already muted, so lets mute
			this.mutelist.put(player.getName()+"."+playeMute, "111");
			PluginUtil.send(player.getName(), PluginUtil.formatUser(playeMute)+ " has been muted");
		}
		
		//save the mutelist
		MutelistManager.save(this.mutelist);
		
		//if (MCNSAChat3.thread != null)
		//	MCNSAChat3.thread.write(new PlayerUpdatePacket(p));
		return true;
	}
}
