package com.aegamesi.mc.mcnsachat3.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MutelistManager {
	public Map<String, String> mutelist = new HashMap<String, String>();
	private MCNSAChat3 plugin;
	public MutelistManager get(MCNSAChat3 plugin){
		
		this.plugin = plugin;
		this.load();
		return null;
	}
	
	public void load(){
		String path = "/plugins/MCNSAChat3/mutelist.bin";
		File file = new File(path);
		if (file.exists()) {
			try { this.mutelist = SLAPI.load(path); } 
			catch (Exception e) { plugin.getLogger().warning("Could not load mutelist "+e.getMessage()); }
		}
		else {
			plugin.getLogger().info("mutelist not found. Creating new");
			this.mutelist = new HashMap<String, String>();
			try { SLAPI.save(this.mutelist, path);}
			catch (Exception e) { plugin.getLogger().warning("Could not save mutelist "+e.getMessage()); }
		}
	}
	
	public void save(){
		String path = "/plugins/MCNSAChat3/mutelist.bin";
		File file = new File(path);
		try { SLAPI.save(this.mutelist, path);}
		catch (Exception e) { plugin.getLogger().warning("Could not save mutelist "+e.getMessage()); }
	}
	public Map<String, String> read() {
		return this.mutelist;
	}
	public void update(Map<String, String> newMutelist){
		this.mutelist = newMutelist;
	}

}
