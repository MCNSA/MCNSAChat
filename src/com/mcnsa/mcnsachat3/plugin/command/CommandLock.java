package com.mcnsa.mcnsachat3.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.packets.PlayerUpdatePacket;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "clock", permission = "lock", usage = "<player> [channel]", description = "locks a player in a channel and optionally moves them")
public class CommandLock implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandLock(MCNSAChat3 plugin) {
		CommandLock.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1)
			return false;

		// process the args
		String[] argParts = sArgs.split(" ");
	
		Player bukkitPlayer = Bukkit.getPlayer(argParts[0]);
		if(bukkitPlayer == null) {
			PluginUtil.send(player.getName(), "&cPlayer not found.");
			return true;
		}

		// get the player
		ChatPlayer targetPlayer = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);

		// check to see if there is the optional channel parameters
		if(argParts.length > 1) {
			// move them straight to the channel, do not check permissions
			targetPlayer.changeChannels(argParts[1]);
		}
		
		// now toggle their locked status
		if(targetPlayer.modes.contains(ChatPlayer.Mode.LOCKED)) {
			PluginUtil.send(targetPlayer.name, "You are no longer locked from changing channels.");
			targetPlayer.modes.remove(ChatPlayer.Mode.LOCKED);
		} else {
			PluginUtil.send(targetPlayer.name, "You have been locked from changing channels.");
			targetPlayer.modes.add(ChatPlayer.Mode.LOCKED);
		}
		
		// notify them
		PluginUtil.send(player.getName(), "Locked " + PluginUtil.formatUser(targetPlayer.name));
		
		// and update them across the servers
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(targetPlayer));
		
		return true;
	}
}
