package com.rock_mc.syn.api;


import org.bukkit.entity.Player;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Logger;


public class Reload {
	private final static String commandName = CmdManager.RELOAD;
	public static void exec(Syn plugin, Logger logger, Player player) {
		if (plugin.cmdManager.lacksPermission(player, commandName)) {
            logger.sendMessage(player, "You don't have permission to use Reload command.");
            return;
        }
        synchronized (Syn.apiLock) {
        	plugin.dbManager.save();
        	plugin.dbManager.close();
        	plugin.configManager.load();
        	plugin.dbManager.load();
        }
        logger.sendMessage(player, "You have reload config!");
    }
	
	public static void exec(Syn plugin, Logger logger, Player player, String[] args) {
        if (args.length > 1) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
            return;
        }
        exec(plugin, logger, player);
    }
	
}
