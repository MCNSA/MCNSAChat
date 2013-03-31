package com.mcnsa.chat.file;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.utilities.Logger;

public class Persistence {
	  private FileConfiguration customConfig = null;
	  private File customConfigFile = null;
	  private MCNSAChat plugin = null;

	  public Persistence(MCNSAChat plugin) {
	    this.plugin = plugin;
	  }

	  public void reload() {
	    if (this.customConfigFile == null)
	      this.customConfigFile = new File(this.plugin.getDataFolder(), "persistence.yml");
	    this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
	  }

	  public FileConfiguration get() {
	    if (this.customConfig == null)
	      reload();
	    return this.customConfig;
	  }

	  public void clear() {
	    this.customConfig = new YamlConfiguration();
	  }

	  public void save() {
	    if ((this.customConfig == null) || (this.customConfigFile == null))
	      return;
	    try {
	      get().save(this.customConfigFile);
	    } catch (IOException ex) {
	      Logger.log("Could not save config to " + this.customConfigFile, ex);
	    }
	  }

	  public void saveDefault() {
	    if (!new File(this.plugin.getDataFolder(), "persistence.yml").exists())
	      this.plugin.saveResource("persistence.yml", false);
	  }
}
