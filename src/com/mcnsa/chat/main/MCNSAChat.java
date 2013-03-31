package com.mcnsa.chat.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.mcnsa.chat.file.Persistence;
import com.mcnsa.chat.managers.ChatManager;
import com.mcnsa.chat.managers.CommandManager;
import com.mcnsa.chat.managers.ComponentManager;
import com.mcnsa.chat.managers.MutelistManager;
import com.mcnsa.chat.managers.PlayerListener;
import com.mcnsa.chat.managers.TimeoutManager;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PlayerRefresh;
import com.mcnsa.chat.utilities.PluginUtil;
import com.mcnsa.chat.client.ClientThread;
import com.mcnsa.chat.chat.ChatChannel;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.managers.ChannelManager;
import com.mcnsa.chat.managers.PlayerManager;

public class MCNSAChat extends JavaPlugin {
	//Keep track of the plugin
	public static MCNSAChat plugin = null;
	//Persistence file
	public static Persistence persist;
	
	public static String name;
	public String longName;
	public static ChatManager chat;
	public static PermissionManager permissions;
	public static ClientThread thread = null;
	public PlayerListener PListener;
	private CommandManager commandManager;
	private ComponentManager componentManager;
	
	
	
	public MCNSAChat() {
		plugin = this;
	}
	
	public void onEnable() {
		//Start up the plugin
		//Load up the persistence file
		persist = new Persistence(this);
		persist.saveDefault();
		saveDefaultConfig();
		
		//Get configs
		name = getConfig().getString("name");
		this.longName = getConfig().getString("longname");
		//Load the player listener
		PListener = new PlayerListener(this);
		
		//initialise the mutelist
		MutelistManager.load(this);
		MutelistManager.save(this);
		
		//initialise the timeouts
		TimeoutManager.load(this);
		TimeoutManager.save(this);
		
		//managers
		chat = new ChatManager(this);
		commandManager = new CommandManager();
		
		//load player manager
		PlayerManager.init();
		
		//load channel manager
		ChannelManager.init();
		
		//send plugin instance to plugin utils
		PluginUtil.plugin = this;
		
		//Permissions
		permissions = PermissionsEx.getPermissionManager();	
		
		// ok, start loading our components
		componentManager = new ComponentManager();
				
		//Load components
		componentManager.initializeComponents();
		//Load our commands
		commandManager.loadCommands(componentManager);
		
		//finally
		loadPlayers();
		loadChannels();
		
		
		//Timed functions
		
		//start the client thread
		final MCNSAChat finalThis = this;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (thread == null) {
					thread = new ClientThread(finalThis);
					thread.start();
				}
			}
		}, 0L, 1200L);
		
		//Run the timeouts manager. See if theres any that have expired
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				TimeoutManager.timer(finalThis);
			}
		}, 0L, 200L);
		
		//Refresh the tab list every 60secs to change colours when needed
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				PlayerRefresh.refreshTabList();
			}
		}, 0L, 1200L);
		
		
		//Log that the plugin is enabled
		Logger.log("Plugin Enabled");
	}
	
	public void onDisable() {
		//Close everything down
		TimeoutManager.save(this);
		MutelistManager.save(this);
		
		// save players
		for (ChatPlayer p : PlayerManager.getPlayersByServer(name)) {
			String pre = "players." + p.name + ".";
			persist.get().set(pre + "channel", p.channel);
			persist.get().set(pre + "listening", p.listening);
			ArrayList<String> modes = new ArrayList<String>();
			for(ChatPlayer.Mode mode : p.modes)
				modes.add(mode.name());
			persist.get().set(pre + "modes", modes);
		}
		// save channels (soft list, will be updated by core on next sync)
		persist.get().set("channels", null);
		ArrayList<HashMap<String, Object>> chanMap = new ArrayList<HashMap<String, Object>>();
		for (ChatChannel c : ChannelManager.channels) {
			if (c.modes.contains(ChatChannel.Mode.PERSIST)) {
				HashMap<String, Object> chan = new HashMap<String, Object>();
				chan.put("name", c.name);
				chan.put("read_permission", c.read_permission);
				chan.put("write_permission", c.write_permission);
				chan.put("alias", c.alias);
				chan.put("color", c.color);
				ArrayList<String> modes = new ArrayList<String>();
				for(ChatChannel.Mode mode : c.modes)
					modes.add(mode.name());
				chan.put("modes", modes);
				chanMap.add(chan);
			}
		}
		persist.get().set("channels", chanMap);
		persist.save();

		//Close server thread
		if (thread != null && thread.socket != null) {
			Logger.log("Socket is being closed NOW!");
			try {
				thread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			thread = null;
		}
		//Log that the plugin is disabled
		Logger.log("Plugin Disabled");
	}
	
	@SuppressWarnings("unchecked")
	public void loadPlayers() {
		ConfigurationSection playerData = persist.get().getConfigurationSection("players");
		if (playerData == null)
			persist.get().createSection("players");
		for (Player player : Bukkit.getOnlinePlayers()) {
			ChatPlayer p = new ChatPlayer(player.getName(), name);
			PlayerManager.players.add(p);
			ConfigurationSection section = playerData.contains(p.name) ? playerData.getConfigurationSection(p.name) : null;

			if (section != null) {
				p.channel = section.getString("channel");
				p.listening.addAll((List<String>) section.get("listening"));
				List<String> modes = (List<String>) section.get("modes");
				for (String mode : modes)
					p.modes.add(ChatPlayer.Mode.valueOf(mode));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadChannels() {
		List<Map<?, ?>> channelData = persist.get().getMapList("channels");
		for (Map<?, ?> channel : channelData) {
		    ChatChannel c = new ChatChannel((String) channel.get("name"));
			c.read_permission = (String) (channel.containsKey("read_permission") ? channel.get("read_permission") : "");
			c.write_permission = (String) (channel.containsKey("write_permission") ? channel.get("write_permission") : "");
			c.alias = (String) (channel.containsKey("alias") ? channel.get("alias") : "");
			c.color = (String) (channel.containsKey("color") ? channel.get("color") : "");
			List<String> modes = (List<String>) channel.get("modes");
			for (String mode : modes)
				c.modes.add(ChatChannel.Mode.valueOf(mode));
			if(c.alias.length() > 0)
				//Set the channel Alias here
			ChannelManager.channels.add(c);
			CommandManager.channelAlias.put(c.alias, c.name);
		}
	}
}
