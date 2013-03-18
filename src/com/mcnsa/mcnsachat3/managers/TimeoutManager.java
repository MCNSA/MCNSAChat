package com.mcnsa.mcnsachat3.managers;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;
import com.mcnsa.mcnsachat3.plugin.SLAPI;

public class TimeoutManager extends Thread {
	
	public MCNSAChat3 plugin;
	public Logger log;
	
	public TimeoutManager (MCNSAChat3 plugin, Logger log){
		this.plugin = plugin;
		this.log = log;
	}
	
	public void run() {
			//Initilise hashmap
				HashMap<String, Long > timeouts = new HashMap<String, Long>();
			//Try loading the hashmap
				try{ timeouts = SLAPI.load("plugins/MCNSAChat3/timeout.bin"); }
				catch(Exception e){ plugin.getLogger().warning("Error loading timeout hashmap. "+e.getMessage()); }
				
			//get current timestamp
				long timeNow = new Date().getTime();
				
				for (String key: timeouts.keySet()) {
					Long timeoutTime = timeouts.get(key);
					if (timeNow >= timeoutTime) {
						timeouts.remove(key);
						//Save the hashmap
							try { SLAPI.save(timeouts, "plugins/MCNSAChat3/timeout.bin");}
								catch (Exception e) { plugin.getLogger().warning("Could not save timeouts file "+e.getMessage()); }
						
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
