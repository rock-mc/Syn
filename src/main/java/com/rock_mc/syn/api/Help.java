package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Log;
import org.bukkit.entity.Player;

public class Help {

    public static void run(Syn plugin, Log log, Player player) {

        String[] allCmds = plugin.cmdManager.getCmdList();

        StringBuilder allCommands = new StringBuilder("Commands:\n");

        for (String cmd : allCmds) {
            allCommands.append(plugin.cmdManager.getCmd(cmd).description).append("\n");
            allCommands.append(plugin.cmdManager.getCmd(cmd).usage).append("\n");
        }

        StringBuilder message = new StringBuilder();
        if (player == null) {
            message = new StringBuilder(allCommands);
        } else if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
            message.append("Commands:\n").append(plugin.cmdManager.getCmd(CmdManager.VERIFY).description);
            message.append("\n").append(plugin.cmdManager.getCmd(CmdManager.VERIFY).usage);

        } else {
            message = new StringBuilder("Commands:");

            for (String cmd : allCmds) {
                if (plugin.cmdManager.getCmd(cmd).permission != null && !player.hasPermission(plugin.cmdManager.getCmd(cmd).permission)) {
                    continue;
                }
                message.append("\n").append(plugin.cmdManager.getCmd(cmd).description);
                message.append("\n").append(plugin.cmdManager.getCmd(cmd).usage);
            }

            if (message.toString().equals("Commands:")) {
                message.append("You don't have permission to use any command.");
            }
        }

        log.sendMessage(player, message.toString().trim());
    }
}


