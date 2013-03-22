package com.mcnsa.mcnsachat3.managers;

import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

public class TimeoutManager extends Thread {
	
	public MCNSAChat3 plugin;
	public Logger log;
	
	public TimeoutManager (MCNSAChat3 plugin, Logger log){
		this.plugin = plugin;
		this.log = log;
	}
	
	public void run() {
				
			//get current timestamp
				long timeNow = new Date().getTime();
				
			//Loop through the timeouts
				for (String key: TimeoutsManager.timeouts.keySet()) {
					
					//Get the time that the player is dure to be untimeouted
					Long timeoutTime = TimeoutsManager.timeouts.get(key);
					if (timeNow >= timeoutTime) {
						//Playe is due to be taken out of timeout
						TimeoutsManager.timeouts.remove(key);
						
						Player bukkitPlayer = Bukkit.getPlayer(key);
						if (bukkitPlayer != null) {
							PluginUtil.send(key, "Your timeout has expired. You can now chat again");
							PluginUtil.send(key+" Has been removed from timeout");
							ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
							p.modes.remove(ChatPlayer.Mode.MUTE);
						}
						
					}
				}
			
		}
		
						
	}
