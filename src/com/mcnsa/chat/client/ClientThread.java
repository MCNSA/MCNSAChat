package com.mcnsa.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.mcnsa.chat.chat.ChatChannel;
import com.mcnsa.chat.chat.ChatPlayer;
import com.mcnsa.chat.client.packets.IPacket;
import com.mcnsa.chat.main.MCNSAChat;
import com.mcnsa.chat.managers.ChannelManager;
import com.mcnsa.chat.managers.PlayerManager;
import com.mcnsa.chat.utilities.Logger;
import com.mcnsa.chat.utilities.PluginUtil;
import com.mcnsa.chat.client.packets.*;


public class ClientThread extends Thread {
	public Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public boolean connected = false;

	public MCNSAChat plugin;

	public ClientThread(MCNSAChat plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Logger.log("Attempting to connect to chat server...");
		try {
			socket = new Socket(plugin.getConfig().getString("chat-server"), 51326);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			Logger.warning("Chat server: Unknown host");
			MCNSAChat.thread = null;
			return;
		} catch (IOException e) {
			Logger.warning("Couldn't connect to chat server");
			MCNSAChat.thread = null;
			return;
		}

		connected = true;
		Logger.log("Connected to chat server.");
		System.out.println("Connected to chat server.");

		try {
			new ServerJoinedPacket(MCNSAChat.name, plugin.longName, PlayerManager.getPlayersByServer(MCNSAChat.name), MCNSAChat.passcode).write(out);
			new ChannelListingPacket(ChannelManager.channels).write(out);

			while (loop(in, out))
				;
		} catch (IOException e) {
			Logger.warning("Chat server: connection lost?");
			String msg = "";
			ArrayList<ChatPlayer> players = (ArrayList<ChatPlayer>) PlayerManager.players.clone();
			for (ChatPlayer p : players) {
				if (!p.server.equals(MCNSAChat.name)) {
					msg += p.name + " ";
					PlayerManager.players.remove(p);
				}
			}
			Logger.log("Players lost: " + msg);

			MCNSAChat.thread = null;
			return;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				socket.close();
				Logger.log("Disconnected from chat server.");
			} catch (IOException e) {
				Logger.warning("Error closing socket");
			}
		}
		MCNSAChat.thread = null;
	}

	public boolean loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			if (packet.shortName.equals(MCNSAChat.name))
				return true;

			// log + notify
			Logger.log("Server joined " + packet.longName);
			String msg = "";
			for (ChatPlayer p : packet.players)
				msg += p.name + " ";
			Logger.log("Players joined: " + msg);

			PlayerManager.players.addAll(packet.players);
			return true;
		}
		if (type == ServerLeftPacket.id) {
			ServerLeftPacket packet = new ServerLeftPacket();
			packet.read(in);
			if (packet.shortName.equals(MCNSAChat.name))
				return true;

			// log + notify
			Logger.log("Server left " + packet.shortName);
			ArrayList<ChatPlayer> playersLost = PlayerManager.getPlayersByServer(packet.shortName);
			String msg = "";
			for (ChatPlayer p : playersLost) {
				msg += p.name + " ";
				PlayerManager.players.remove(p);
			}
			Logger.log("Players left: " + msg);
			return true;
		}
		if (type == ChannelListingPacket.id) {
			ChannelListingPacket packet = new ChannelListingPacket();
			packet.read(in);

			// log + notify
			Logger.log("Received updated channel list");
			ChannelManager.channels = packet.channels;
			return true;
		}
		if (type == PlayerJoinedPacket.id) {
			PlayerJoinedPacket packet = new PlayerJoinedPacket();
			packet.read(in);
			if (packet.player.server.equals(MCNSAChat.name))
				return true;

			// log + notify
			Logger.log(packet.player.name + " joined " + packet.longname);
			if (!ChannelManager.getChannel(packet.player.channel).modes.contains(ChatChannel.Mode.LOCAL)) {
				String joinString = plugin.getConfig().getString("strings.player-join");
				joinString = joinString.replaceAll("%prefix%", MCNSAChat.permissions.getUser(packet.player.name).getPrefix());
				joinString = joinString.replaceAll("%player%", packet.player.name);
				joinString = joinString.replace("%server%", packet.longname);
				ArrayList<ChatPlayer> toNotify = PlayerManager.getPlayersListeningToChannel(packet.player.channel);
				for (ChatPlayer p : toNotify)
					if (p.server.equals(MCNSAChat.name))
						PluginUtil.sendLater(p.name, joinString);
			}

			PlayerManager.players.add(packet.player);
			return true;
		}
		if (type == PlayerLeftPacket.id) {
			PlayerLeftPacket packet = new PlayerLeftPacket();
			packet.read(in);
			if (packet.player.server.equals(MCNSAChat.name))
				return true;

			// log + notify
			Logger.log(packet.player.name + " left " + packet.longname);
			if (!ChannelManager.getChannel(packet.player.channel).modes.contains(ChatChannel.Mode.LOCAL)) {
				String quitString = plugin.getConfig().getString("strings.player-quit");
				quitString = quitString.replaceAll("%prefix%", MCNSAChat.permissions.getUser(packet.player.name).getPrefix());
				quitString = quitString.replaceAll("%player%", packet.player.name);
				quitString = quitString.replace("%server%", packet.longname);
				ArrayList<ChatPlayer> toNotify = PlayerManager.getPlayersListeningToChannel(packet.player.channel);
				for (ChatPlayer p : toNotify)
					if (p.server.equals(MCNSAChat.name))
						PluginUtil.sendLater(p.name, quitString);
			}

			PlayerManager.removePlayer(packet.player);
			return true;
		}
		if (type == PlayerUpdatePacket.id) {
			PlayerUpdatePacket packet = new PlayerUpdatePacket();
			packet.read(in);
			if (packet.player.server.equals(MCNSAChat.name))
				return true;

			// log + notify
			Logger.log("Updated player " + packet.player.name + " on " + packet.player.server);
			// this usually signifies a mode change or channel change. We don't
			// really care, however, as it is on another server
			PlayerManager.removePlayer(packet.player);
			PlayerManager.players.add(packet.player);
			return true;
		}
		if (type == ChannelUpdatePacket.id) {
			ChannelUpdatePacket packet = new ChannelUpdatePacket();
			packet.read(in);

			// log + notify
			Logger.log("Updated channel" + packet.channel.name);
			// this usually signifies a mode change or something
			ChannelManager.removeChannel(packet.channel);
			ChannelManager.channels.add(packet.channel);
			return true;
		}
		if (type == PlayerChatPacket.id) {
			PlayerChatPacket packet = new PlayerChatPacket();
			packet.read(in);
			if (packet.player.server.equals(MCNSAChat.name))
				return true;
			if (ChannelManager.getChannel(packet.channel == null ? packet.player.channel : packet.channel).modes.contains(ChatChannel.Mode.LOCAL))
				return true;

			if (packet.type == PlayerChatPacket.Type.CHAT)
				MCNSAChat.chat.chat(packet.player, packet.message, packet.channel);
			if (packet.type == PlayerChatPacket.Type.ACTION)
				MCNSAChat.chat.action(packet.player, packet.message, packet.channel);
			if (packet.type == PlayerChatPacket.Type.MISC)
				MCNSAChat.chat.info(null, packet.message, packet.channel, true);
			return true;
		}
		if (type == PlayerPMPacket.id) {
			PlayerPMPacket packet = new PlayerPMPacket();
			packet.read(in);
			if (packet.from.server.equals(MCNSAChat.name))
				return true;

			Logger.log("[" + packet.from.name + " -> " + packet.to + "] " + packet.message);
			if (Bukkit.getPlayerExact(packet.to) != null)
				MCNSAChat.chat.pm_receive(packet.from, packet.to, packet.message);
			return true;
		}
		return false;
	}

	public void write(IPacket packet) {
		if (!connected)
			return;
		try {
			packet.write(out);
		} catch (IOException e) {
			Logger.warning("Error writing packet " + packet.getClass() + ". Stack Trace Below:");
			Logger.warning(e.getMessage());
			MCNSAChat.thread = null;
			try {
				socket.close();
			} catch (IOException e1) {
			}
		}
	}
}
