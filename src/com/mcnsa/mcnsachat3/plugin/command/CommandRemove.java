package com.mcnsa.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "ckick", permission = "remove", usage = "<player> <channel>", description = "Removes a player from a channel")
public class CommandRemove implements Command{
	private static MCNSAChat3 plugin;

	public CommandRemove(MCNSAChat3 plugin) {
		 CommandRemove.plugin = plugin;
	}
	
	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1)
			return false;
		
		String[] args = sArgs.split(" ");
		if (args.length < 2) {
			return false;
		}
		
		//Get the player they are trying to remove from the channel
		ArrayList<ChatPlayer> playerRemove = PlayerManager.getPlayersByFuzzyName(args[0]);
		if (playerRemove.isEmpty()) {
			//Could not find player
			PluginUtil.send(player.getName(), "Could not find player");
			return true;
		}
		else {
			//Found player
			ChatPlayer playerRemoval = playerRemove.get(0);
			playerRemoval.listening.remove(args[1].toLowerCase());
			
			PluginUtil.send(playerRemoval.name, "You are no longer listening to "+args[1]);
			PluginUtil.send(player.getName(), playerRemoval.name+" is no longer listening to "+args[1]);
			plugin.getLogger().info(player.getName() +"removed "+playerRemoval.name+" from "+ args[1]);
			
			if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(playerRemoval));
		}
		
		return true;
	}
	
}
