package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import com.rock_mc.syn.event.pluginevent.KickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


public class Ban {

    private final static String commandName = CmdManager.BAN;

    public static boolean exec(Syn plugin, Logger logger, Player player, String banPlayerName, String reason, Long banSecs) {
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

        reason = (reason == null ? "Admin Ban" : reason);

        plugin.dbManager.addPlayerToBannedList(pluginPlayerInfo.getPlayer_uuid(), reason, banSecs);
        plugin.dbManager.removePlayerFailedList(pluginPlayerInfo.getPlayer_uuid());

        if (Bukkit.getOfflinePlayer(pluginPlayerInfo.getUUID()).isOnline()) {
            // 如果在線上踢掉
            // 順便告訴他刑期，很棒吧
            String blockMsg;
            if (banSecs == 0) {
                blockMsg = "你被永久加入黑名單";
            } else {
                blockMsg = "你被加入黑名單，刑期 " + Utils.timeToStr(banSecs);
            }

            Player banPlayer = Bukkit.getPlayer(pluginPlayerInfo.getUUID());

            Event event = new KickEvent(false, banPlayer, blockMsg);
            Bukkit.getPluginManager().callEvent(event);
        }

        logger.sendMessage(player, "將使用者加入黑名單" + ChatColor.RED + banPlayerName);
        return true;
    }
}
