package com.aegamesi.mc.mcnsachat3.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class MutelistManager {
	public static Map<String, String> mutelist = new HashMap<String, String>();
	private static MCNSAChat3 plugin;

	public static Map<String, String> load(){
		String path = "/plugins/MCNSAChat3/mutelist.bin";
		File file = new File(path);
		if (file.exists()) {
			try { mutelist = SLAPI.load(path); } 
			catch (Exception e) { plugin.getLogger().warning("Could not load mutelist "+e.getMessage()); }
		}
		else {
			Bukkit.getLogger().info("mutelist not found. Creating new");
			mutelist = new HashMap<String, String>();
			try { SLAPI.save(mutelist, path);}
			catch (Exception e) { Bukkit.getLogger().warning("Could not save mutelist "+e.getMessage()); }
		}
		return mutelist;
	}
	
	public static void save(Map mutelist){
		String path = "/plugins/MCNSAChat3/mutelist.bin";
		File file = new File(path);
		try { SLAPI.save(mutelist, path);}
		catch (Exception e) { plugin.getLogger().warning("Could not save mutelist "+e.getMessage()); }
	}

}
