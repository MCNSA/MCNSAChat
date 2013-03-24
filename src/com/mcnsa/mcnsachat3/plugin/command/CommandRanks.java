package com.mcnsa.mcnsachat3.plugin.command;

import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionGroup;

import com.mcnsa.mcnsachat3.plugin.MCNSAChat3;
import com.mcnsa.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "ranks", permission = "", description = "lists all of the server ranks")
public class CommandRanks implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandRanks(MCNSAChat3 plugin) {
		CommandRanks.plugin = plugin;
	}

	public Boolean handle(CommandSender sender, String sArgs) {
		PermissionGroup[] groups = MCNSAChat3.permissions.getGroups();
		Arrays.sort(groups, new Comparator<PermissionGroup>() {
			public int compare(PermissionGroup a, PermissionGroup b) {
				int ra = a.getOptionInteger("rank", "", 9999);
				int rb = b.getOptionInteger("rank", "", 9999);
				return (ra < rb ? 1 : (ra > rb ? -1 : 0));
			}
		});

		String ranks = "&fRanks in order of least to greatest: ";
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getOptionInteger("rank", "", 9999) != 9999) 
				ranks += groups[i].getPrefix() + groups[i].getName() + "&f" + (i == groups.length - 1 ? "" : ", ");
		PluginUtil.send(sender, ranks);
		return true;
	}
}
