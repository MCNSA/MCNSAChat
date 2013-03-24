package com.mcnsa.mcnsachat3.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.mcnsa.mcnsachat3.chat.ChatChannel;
import com.mcnsa.mcnsachat3.chat.ChatPlayer;
import com.mcnsa.mcnsachat3.managers.ChannelManager;
import com.mcnsa.mcnsachat3.managers.ChatManager;
import com.mcnsa.mcnsachat3.managers.CommandManager;
import com.mcnsa.mcnsachat3.managers.PlayerListener;
import com.mcnsa.mcnsachat3.managers.PlayerManager;
import com.mcnsa.mcnsachat3.managers.TimeoutManager;
import com.mcnsa.mcnsachat3.managers.TimeoutsManager;

public final class MCNSAChat3 extends JavaPlugin implements Listener {
	public static ClientThread thread = null;
	public String name;
	public String longname;
	public MutelistManager muteManager;
	public ChatManager chat;
	public CommandManager command;
	public PlayerListener pHandler;
	public static Persistence persist;
	public static PermissionManager permissions;
	private FileHandler fileHandler;
	public Map<String, String> mutelist = new HashMap<String, String>();
	public Map<String, Long> timeouts = new HashMap<String, Long>();

	public void onEnable() {
		
		persist = new Persistence(this);
		persist.saveDefault();
		saveDefaultConfig();

		// whew all the handlers and managers...mostly
			//Server name
				name = getConfig().getString("name");
				longname = getConfig().getString("longname");
		
			//Player handler
				pHandler = new PlayerListener(this);
			//Chat manager
				chat = new ChatManager(this);
			//Commant manager
				command = new CommandManager(this);
			//Player manager
				PlayerManager.init();
			//Channel manager
				ChannelManager.init();
			//send plugin instance to pluginutils
				PluginUtil.plugin = this;
			//Permissions manager
				permissions = PermissionsEx.getPermissionManager();
		
		getLogger().setUseParentHandlers(false);
		try {
			fileHandler = new FileHandler(getDataFolder() + "/chat3.log", 1024 * 1024 * 1024, 1);
			fileHandler.setFormatter(new LogFormatter());
			getLogger().addHandler(fileHandler);
		}
		catch (IOException e) {
			System.out.println("Error opening log file, redirecting output to console.");
			getLogger().setUseParentHandlers(true);
		}
		catch(SecurityException e) {
			System.out.println("Error opening log file, redirecting output to console.");
			getLogger().setUseParentHandlers(true);
		}
		
		//mutelist
			
		this.mutelist = MutelistManager.load(this);
		
		//timeouts
		TimeoutsManager.load(this);
		
		// persistence
		loadPlayers();
		loadChannels();
		
		// set up colored ranks and stuff
		Player[] players = getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {			
			String result = PluginUtil.color(PluginUtil.formatUser(players[i].getName()));
			if(result.length() > 16)
				result = result.substring(0, 16);
			players[i].setPlayerListName(result);
		}

		// start connecting to server
		final MCNSAChat3 finalThis = this;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (thread == null) {
					thread = new ClientThread(finalThis, finalThis.getLogger());
					thread.start();
				}
			}
		}, 0L, 200L);
		
		//Timeouts
		Bukkit.getScheduler().scheduleSyncRepeatingTask(finalThis, new Runnable() {  
			
			public void run() {
				TimeoutManager timeoutTask = new TimeoutManager(finalThis, getLogger());
				timeoutTask.start();
			}
			
			}, 0L, 600L);
		
		//Tab list refreshing
		Bukkit.getScheduler().scheduleSyncRepeatingTask(finalThis, new Runnable() {  
			
			public void run() {
				PlayerRefresh.refreshTabList();
			}
			
			}, 0L, 600L);
	}

	@SuppressWarnings("unchecked")
	public void loadPlayers() {
		ConfigurationSection playerData = persist.get().getConfigurationSection("players");
		if (playerData == null) {
			persist.get().createSection("players");
			// fix null pointer exception
			playerData = persist.get().getConfigurationSection("players");
		}
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
				command.aliases.put(c.alias, c.name);
			ChannelManager.channels.add(c);
		}
	}

	public void onDisable() {
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
		persist.get().set("channels", chanMap);
		persist.save();
		
		//Save timeouts
		TimeoutsManager.save(this);

		if (thread != null && thread.socket != null) {
			getLogger().info("Socket is being closed NOW!");
			try {
				thread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			thread = null;
		}
		fileHandler.close();
	}
	
	public static boolean hasPermission(CommandSender sender, String permission) {
		if(sender instanceof Player) {
			return permissions.has((Player)sender, permission);
		}
		return true;
	}
	
	// handle commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return command.handleCommand(sender, cmd.getName() + " " + PluginUtil.implode(" ", args));
	}
}