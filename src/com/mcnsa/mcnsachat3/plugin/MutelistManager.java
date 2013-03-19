package com.mcnsa.mcnsachat3.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class MutelistManager {
	public static Map<String, String> mutelist = new HashMap<String, String>();
	private static MCNSAChat3 plugin;

	public static Map<String, String> load(){
		//Loads the mutelist hashmap
			String path = plugin.getDataFolder()+"/mutelist.bin";
			File file = new File(path);
			if (file.exists()) {
				//Theres already a hashmap. So load it
					try { mutelist = SLAPI.load(path); } 
						catch (Exception e) { plugin.getLogger().warning("Could not load mutelist "+e.getMessage()); }
		}
		else {
			//Need to create a hashmap
				Bukkit.getLogger().info("mutelist not found. Creating new");
				mutelist = new HashMap<String, String>();
			//Now saving the map
				try { SLAPI.save(mutelist, path);}
					catch (Exception e) { Bukkit.getLogger().warning("Could not save mutelist "+e.getMessage()); }
		}
		
		//Return it
		return mutelist;
	}
	
	public static void save(Map mutelist){
		//Function to save the map
		String path = plugin.getDataFolder()+"/mutelist.bin";
		File file = new File(path);
		try { SLAPI.save(mutelist, path);}
		catch (Exception e) { plugin.getLogger().warning("Could not save mutelist "+e.getMessage()); }
	}

}
