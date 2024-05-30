package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.log.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class Info {
    private final static String commandName = CmdManager.INFO;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Info.class);

    public static void exec(Syn plugin, Logger logger, Player player, String playerName) {

        PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(playerName);
        if (pluginPlayerInfo == null) {
            logger.sendMessage(player, "查無此玩家" + playerName);
            logger.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
            return;
        }

        logger.sendMessage(player, plugin.getDescription().getVersion());
        logger.sendMessage(player, "使用者: " + ChatColor.GREEN + playerName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        logger.sendMessage(player, "最近登入" + sdf.format(pluginPlayerInfo.getLast_login()));
        logger.sendMessage(player, "加入時間" + sdf.format(pluginPlayerInfo.getCreated_at()));
    }
}
