package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.event.pluginevent.KickEvent;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


public class Ban {

    private final static String commandName = CmdManager.BAN;

    public static boolean exec(Syn plugin, Logger logger, Player player, String banPlayerName, String reason, String banTime) {

        synchronized (Syn.apiLock) {

            if (plugin.cmdManager.lacksPermission(player, commandName)) {
                logger.sendMessage(player, "You don't have permission to use this command.");
                return false;
            }

            PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(banPlayerName);
            if (pluginPlayerInfo == null) {
                logger.sendMessage(player, "查無此玩家: " + banPlayerName);
                logger.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
                return false;
            }

            long banSecs = Utils.strToTime(banTime);

            plugin.dbManager.addPlayerToBannedList(pluginPlayerInfo.getPlayer_uuid(), reason, banSecs);
            plugin.dbManager.removePlayerFailedList(pluginPlayerInfo.getPlayer_uuid());

            if (Bukkit.getOfflinePlayer(pluginPlayerInfo.getUUID()).isOnline()) {
                // 如果在線上踢掉
                // 順便告訴他刑期，很棒吧
                String banMsg;
                if (banSecs == 0) {
                    banMsg = (reason == null ? "你被永久加入禁止名單" : reason);
                } else {
                    banMsg = (reason == null ? "你被加入禁止名單" : reason) + "，刑期 " + Utils.timeToStr(banSecs);
                }

                Player banPlayer = Bukkit.getPlayer(pluginPlayerInfo.getUUID());

                Event kickEvent = new KickEvent(false, banPlayer, banMsg);
                Bukkit.getPluginManager().callEvent(kickEvent);
            }

            logger.sendMessage(player, "將使用者加入禁止名單: " + ChatColor.RED + banPlayerName);
            return true;
        }
    }

    public static boolean exec(Syn plugin, Logger logger, Player player, String[] args) {
        if (args.length < 1) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.BAN).usage);
            return false;
        }
        if (args.length > 4) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.BAN).usage);
            return false;
        }

        String banPlayerName = args[1];
        String reason = "Admin Ban";
        String banTime = "0s"; // 0s means permanent ban
        if (args.length == 3) {
            // the parameter can be either the reason or the ban time
            String temp = args[2];

            if (Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                // it is a time string
                banTime = temp;
            } else {
                // it is a reason
                reason = temp;
            }
        } else if (args.length == 4) {
            String temp = args[2];

            if (Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                // it is a time string
                banTime = temp;
                reason = args[3];
            } else {
                // it is a reason
                banTime = args[3];
                reason = temp;
            }
        }

        return exec(plugin, logger, player, banPlayerName, reason, banTime);
    }
}
