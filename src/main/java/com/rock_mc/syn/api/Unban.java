package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.log.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Unban {
    private final static String commandName = CmdManager.UNBAN;

    public static boolean exec(Syn plugin, Logger logger, Player player, String bannedPlayerName) {

        synchronized (Syn.apiLock) {
            if (plugin.cmdManager.lacksPermission(player, commandName)) {
                logger.sendMessage(player, "You don't have permission to use this command.");
                return false;
            }

            PluginPlayerInfo unblockPlayer = plugin.dbManager.getPlayerByName(bannedPlayerName);
            if (unblockPlayer == null) {
                logger.sendMessage(player, "查無此玩家: " + bannedPlayerName);
                return false;
            }

            plugin.dbManager.removePlayerBannedList(unblockPlayer.getPlayer_uuid());

            logger.sendMessage(player, "將使用者移出禁止名單" + ChatColor.GREEN + bannedPlayerName);
            return true;
        }
    }

    public static boolean exec(Syn plugin, Logger logger, Player player, String[] args) {
        if (args.length != 2) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.UNBAN).usage);
            return false;
        }

        String bannedPlayerName = args[1];

        return exec(plugin, logger, player, bannedPlayerName);
    }
}
