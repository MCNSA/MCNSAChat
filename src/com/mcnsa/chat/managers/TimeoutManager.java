package com.mcnsa.chat.managers;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;
import com.mcnsa.chat.utilities.Slapi;

public class TimeoutManager {
	public static Map<String, Long> timeouts;
	@SuppressWarnings("unchecked")
	public static void load(MCNSAChat plugin)
	  {
	    String path = plugin.getDataFolder() + "/timeouts.bin";
	    File file = new File(path);
	    if (file.exists()) {
	      try {
	        timeouts = (Map<String, Long>) Slapi.load(path); } catch (Exception e) {
	        Logger.warning("Could not load timeouts " + e.getMessage());
	      }
	    }
	    else {
	    	Logger.log("timeouts not found. Creating new");
	      timeouts = new HashMap<String, Long>();
	      try {
	        Slapi.save(timeouts, path); } catch (Exception e) {
	        Logger.warning("Could not save timeouts " + e.getMessage());
	      }
	    }
	  }

	  public static void save(MCNSAChat plugin)
	  {
	    String path = plugin.getDataFolder() + "/timeouts.bin";
	    try { Slapi.save(timeouts, path); } catch (Exception e) {
	    Logger.warning("Could not save timeouts " + e.getMessage());
	    }
	  }
	  
	  public static void timer(MCNSAChat plugin) {
		  //This function removes expired timeouts.
		//get current timestamp
			long timeNow = new Date().getTime();
			
		//Loop through the timeouts
			for (String key: TimeoutManager.timeouts.keySet()) {
				
				//Get the time that the player is dure to be untimeouted
				Long timeoutTime = TimeoutManager.timeouts.get(key);
				if (timeNow >= timeoutTime) {
					//Playe is due to be taken out of timeout
					TimeoutManager.timeouts.remove(key);
					
					Player bukkitPlayer = Bukkit.getPlayer(key);
					if (bukkitPlayer != null) {
						PluginUtil.send(key, "Your timeout has expired. You can now chat again");
						PluginUtil.send(key+" Has been removed from timeout");
						ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), MCNSAChat.name);
						p.modes.remove(ChatPlayer.Mode.MUTE);
					}
					
				}
			}
		
	  }
}
