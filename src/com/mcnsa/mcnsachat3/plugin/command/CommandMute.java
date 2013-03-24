package com.mcnsa.mcnsachat3.plugin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.MutelistManager;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;


@Command.CommandInfo(alias = "cmute", permission = "mute", usage = "<player>", description = "Mute a player so you dont have to listen to them", playerOnly = true)
public class CommandMute implements Command {
	Map<String, String> mutelist = new HashMap<String, String>();
	
	public static MCNSAChat3 plugin = null;
	
	public CommandMute(MCNSAChat3 plugin) {
		 CommandMute.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		
		if(sArgs.length() < 1)
			return false;

		
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByFuzzyName(sArgs);
		if(tos.size() == 0) {
			PluginUtil.send(sender, "&cPlayer not found");
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
			PluginUtil.send(sender, matches);
			return true;
		}
		String playeMute = uniques.get(0);
		
		//This is where we set if the player is muted or not
		this.mutelist = MutelistManager.load(plugin);
		if (this.mutelist.containsKey(sender+"."+playeMute)) {
			//Player is already muted so lets remove
			this.mutelist.remove(sender+"."+playeMute);
			PluginUtil.send(sender, PluginUtil.formatUser(playeMute)+ " has been unmuted");
		}
		else {
			//Player is not already muted, so lets mute
			this.mutelist.put(sender.getName()+"."+playeMute, "111");
			PluginUtil.send(sender, PluginUtil.formatUser(playeMute)+ " has been muted");
		}
		
		//save the mutelist
		MutelistManager.save(this.mutelist, plugin);
		
		return true;
	}
}
