package com.mcnsa.chat.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.Slapi;

public class MutelistManager {
	public static Map<String, String> mutelist;
	
	@SuppressWarnings("unchecked")
	public static void load(MCNSAChat plugin)
	  {
	    String path = plugin.getDataFolder() + "/mutelist.bin";
	    File file = new File(path);
	    if (file.exists()) {
	      try {
	        mutelist = (Map<String, String>) Slapi.load(path); } catch (Exception e) {
	        Logger.log("Could not load mutelist " + e.getMessage());
	      }
	    }
	    else {
	    	Logger.log("mutelist not found. Creating new");
	      mutelist = new HashMap<String, String>();
	      try {
	        Slapi.save(mutelist, path); } catch (Exception e) {
	        Logger.log("Could not save mutelist " + e.getMessage());
	      }
	    }
	  }

	  public static void save(MCNSAChat plugin)
	  {
	    String path = plugin.getDataFolder() + "/mutelist.bin";
	    try { Slapi.save(mutelist, path); } catch (Exception e) {
	    Logger.log("Could not save mutelist " + e.getMessage());
	    }
	  }
}
