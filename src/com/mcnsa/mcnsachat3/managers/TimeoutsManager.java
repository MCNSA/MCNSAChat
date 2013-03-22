package com.mcnsa.mcnsachat3.managers;

import java.io.File;
import java.util.HashMap;

import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.SLAPI;

public class TimeoutsManager {
	
	public static HashMap<String, Long > timeouts;
	
	public static void load(MCNSAChat3 plugin) {
		//Try loading the hashmap
		
		String path = plugin.getDataFolder()+"/timeout.bin";
		File file = new File(path);
		if (file.exists()) {
			try{ timeouts = SLAPI.load(path); }
			catch(Exception e){ plugin.getLogger().warning("Error loading timeout hashmap. "+e.getMessage()); }
		}
		else {
			timeouts = new HashMap<String, Long>();
		}
	}
	
	public static void save(MCNSAChat3 plugin) {
		String path = plugin.getDataFolder()+"/timeout.bin";		
		try { SLAPI.save(timeouts, path);}
		catch (Exception e) { plugin.getLogger().warning("Could not save timeouts "+e.getMessage()); }
	}
}
