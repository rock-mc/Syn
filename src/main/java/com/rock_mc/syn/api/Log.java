package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Log {
    private final static String commandName = CmdManager.LOG;

    public static void exec(Syn plugin, Logger logger, Player player, String[] args) {
        synchronized (Syn.apiLock) {
            // TODO: syn log t:[time] u:[player] p:[page]

            long[] times = Utils.parseTime(args);
            Timestamp start = null;
            Timestamp end = null;

            if (times.length == 2) {
                start = new Timestamp(System.currentTimeMillis() - times[1]);
                end = new Timestamp(System.currentTimeMillis() - times[0]);
            }

//            PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(playerName);
//            if (pluginPlayerInfo == null) {
//                logger.sendMessage(player, "查無此玩家" + playerName);
//                logger.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
//                return;
//            }

//            logger.sendMessage(player, plugin.getDescription().getVersion());
//            logger.sendMessage(player, "使用者: " + ChatColor.GREEN + playerName);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            logger.sendMessage(player, "開始:" + sdf.format(start));
            logger.sendMessage(player, "結束:" + sdf.format(end));

        }
    }
}
