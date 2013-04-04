package com.mcnsa.chat.managers;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcnsa.chat.chat.ChatChannel;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.client.packets.PlayerChatPacket;
import com.mcnsa.chat.client.packets.PlayerJoinedPacket;
import com.mcnsa.chat.client.packets.PlayerLeftPacket;
import com.mcnsa.chat.client.packets.PlayerUpdatePacket;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;

public class PlayerListener implements Listener {
	public MCNSAChat plugin;

	public PlayerListener(MCNSAChat plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void loginHandler(PlayerLoginEvent evt) {
		// see if they are allowed to login
	}

	@SuppressWarnings({ "unchecked" })
	@EventHandler(priority = EventPriority.MONITOR)
	public void joinHandler(PlayerJoinEvent evt) {
		evt.setJoinMessage("");

		ChatPlayer p = new ChatPlayer(evt.getPlayer().getName(), MCNSAChat.name);
		p.formatted = PluginUtil.formatUser(p.name);
		boolean welcomeThem = false;
		// load data for the player, if it exists
		ConfigurationSection playerData = MCNSAChat.persist.get().getConfigurationSection("players");
		if (playerData.contains(p.name)) {
			ConfigurationSection section = playerData.getConfigurationSection(p.name);
			p.channel = section.getString("channel");
			p.listening.addAll((List<String>) section.get("listening"));
			if (ChannelManager.getChannel(p.channel) == null) {
				//channel does not exist. Add new
				ChannelManager.channels.add(new ChatChannel(p.channel));
			}
		} else {
			// use default info
			p.channel = plugin.getConfig().getString("default-channel");
			p.listening.addAll((List<String>) plugin.getConfig().getList("default-listen"));
			welcomeThem = true;
			if (ChannelManager.getChannel(p.channel) == null) {
				//channel does not exist. Add new
				ChannelManager.channels.add(new ChatChannel(p.channel));
			}
		}
		
		
		PlayerManager.players.add(p);
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerJoinedPacket(p, plugin.longName));
		// tell *everybody!*
		String joinString = plugin.getConfig().getString("strings.player-join");
		joinString = joinString.replaceAll("%prefix%", MCNSAChat.permissions.getUser(evt.getPlayer()).getPrefix());
		joinString = joinString.replaceAll("%player%", evt.getPlayer().getName());
		joinString = joinString.replaceAll("%server%", plugin.longName);
		PluginUtil.send(joinString);
		if (welcomeThem) {
			String welcomeString = plugin.getConfig().getString("strings.player-welcome");
			welcomeString = welcomeString.replaceAll("%player%", evt.getPlayer().getName());
			for (Player player : Bukkit.getOnlinePlayers())
				if (!player.getName().equals(evt.getPlayer().getName()))
					PluginUtil.send(player.getName(), welcomeString);

		}

		// welcome them, send list of players, set colored name
			String result = PluginUtil.color(PluginUtil.formatUser(evt.getPlayer().getName()));
			if (result.length() > 16)
				result = result.substring(0, 16);
		//This sets the colour in the tab player list.
			evt.getPlayer().setPlayerListName(result);
			
		//Check to see if the config is set to show /list on player join
		if (!(plugin.getConfig().getBoolean("hide-playerlist-onJoin"))) {	
			//Config says yes. Display the list of players
			PluginUtil.send(evt.getPlayer().getName(), PluginUtil.getPlayerList());
		}
		
		//Timeout handling
						
			//get current timestamp
				long timeNow = new Date().getTime();
				
				for (String key: TimeoutManager.timeouts.keySet()) {
					Long timeoutTime = TimeoutManager.timeouts.get(key);
					if (key.contains(p.name)){
						//Player is in the timeoutlist
						if (timeNow >= timeoutTime) {
							//Timeout has expired
							TimeoutManager.timeouts.remove(key);
						}
						else {
							//Timeout is still active
							p.modes.add(ChatPlayer.Mode.MUTE);
							PluginUtil.send(key, "Your timeout is still active. You cannot chat yet");
						}
					}
				}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void quitHandler(PlayerQuitEvent evt) {
		evt.setQuitMessage("");

		ChatPlayer p = PlayerManager.getPlayer(evt.getPlayer().getName(), MCNSAChat.name);
		PlayerManager.removePlayer(p);
		// persist
		String pre = "players." + p.name + ".";
		MCNSAChat.persist.get().set(pre + "channel", p.channel);
		MCNSAChat.persist.get().set(pre + "listening", p.listening);
		// network
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerLeftPacket(p, plugin.longName));
		// tell *everybody!*
		String quitString = plugin.getConfig().getString("strings.player-quit");
		quitString = quitString.replaceAll("%prefix%", MCNSAChat.permissions.getUser(evt.getPlayer()).getPrefix());
		quitString = quitString.replaceAll("%player%", evt.getPlayer().getName());
		quitString = quitString.replaceAll("%server%", plugin.longName);
		PluginUtil.send(quitString);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void chatHandler(AsyncPlayerChatEvent evt) {
		if (evt.isCancelled())
			return;
		evt.setCancelled(true);
		ChatPlayer player = PlayerManager.getPlayer(evt.getPlayer().getName(), MCNSAChat.name);
		// XXX blah blah check some stuff, like timeout maybe? are they allowed
		// to chat?
		String write_perm = ChannelManager.getChannel(player.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat.permissions.has(evt.getPlayer(), "mcnsachat.write." + write_perm)) {
			plugin.getLogger().info(player.name + " attempted to write to channel " + player.channel + " without permission!");
			PluginUtil.send(player.name, "&cYou don't have permission to do that!");
			return;
		}
		if(player.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(player.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(player.name, "You are not allowed to speak right now.");
			return;
		}
		MCNSAChat.chat.chat(player, evt.getMessage(), null);
		// tell *everybody!*
		if (MCNSAChat.thread != null)
			MCNSAChat.thread.write(new PlayerChatPacket(player, evt.getMessage(), null, PlayerChatPacket.Type.CHAT));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void tabCompleteHandler(PlayerChatTabCompleteEvent evt) {
		if (evt.getChatMessage().startsWith("/") && evt.getChatMessage().indexOf(" ") < 0) {
			// it's a command
			return;
		} else {
			evt.getTabCompletions().clear();
			String token = evt.getLastToken().toLowerCase();
			for (ChatPlayer player : PlayerManager.players) {
				if (player.name.toLowerCase().startsWith(token))
					evt.getTabCompletions().add(player.name);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void preprocessHandler(PlayerCommandPreprocessEvent evt) {
		if (evt.isCancelled())
			return;
	String[] args = evt.getMessage().split(" ");
	String command = args[0].substring(1);
	if (CommandManager.channelAlias.containsKey(command)) {
		//Channel alias
			
		if (args.length > 1) {
			//contains message
			//Build the message
			StringBuilder sb = new StringBuilder();
			for(int i = 1; i < args.length; i++ ){
				
				sb.append(args[i]+" ");
			}
			String message = sb.toString();
			
			//Get the player
			ChatPlayer player = PlayerManager.getPlayer(evt.getPlayer().getName(), MCNSAChat.name);
			String write_perm = ChannelManager.getChannel(CommandManager.channelAlias.get(command)).write_permission;
			
			//Check for permissions
			if (!write_perm.equals("") && !MCNSAChat.permissions.has(evt.getPlayer(), "mcnsachat.write." + write_perm)) {
				plugin.getLogger().info(player.name + " attempted to write to channel " + CommandManager.channelAlias.get(command) + " without permission!");
				PluginUtil.send(player.name, "&cYou don't have permission to do that!");
				return;
			}
			
			//Check for mute modes
			if(player.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(CommandManager.channelAlias.get(command)).modes.contains(ChatChannel.Mode.MUTE)) {
				PluginUtil.send(player.name, "You are not allowed to speak right now.");
				return;
			}
			
			//Write the message
			MCNSAChat.chat.chat(player, message, CommandManager.channelAlias.get(command));
			//Notify other servers
			if (MCNSAChat.thread != null)
				MCNSAChat.thread.write(new PlayerChatPacket(player, message, CommandManager.channelAlias.get(command), PlayerChatPacket.Type.CHAT));
		}
		else {
			//get player
			ChatPlayer cp = PlayerManager.getPlayer(evt.getPlayer().getName(), MCNSAChat.name);
			//Change channels
			cp.changeChannels(CommandManager.channelAlias.get(command));
			//Notify other servers
			if (MCNSAChat.thread != null)
				MCNSAChat.thread.write(new PlayerUpdatePacket(cp));
		}
		evt.setCancelled(true);
	}
	
	
	}
}

