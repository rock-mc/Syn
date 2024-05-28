package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.log.LogProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class CmdInfo {
    private final static String commandName = CmdManager.INFO;

    public static void run(Syn plugin, LogProvider log, Player player, String[] args) {
        PlayerInfo playerInfo = null;
        if (args.length == 2) {
            String playerName = args[1];

            log.sendMessage(player, "查詢使用者" + ChatColor.GREEN + playerName);

            // 取得玩家資料
            playerInfo = plugin.dbManager.getPlayerByName(playerName);
            if (playerInfo == null) {
                log.sendMessage(player, "查無此玩家" + playerName);

            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                log.sendMessage(player, "最近登入" + sdf.format(playerInfo.getLast_login()));
                log.sendMessage(player, "加入時間" + sdf.format(playerInfo.getCreated_at()));

            }
        } else {
            log.sendMessage(player, "Syn version: " + plugin.getServer().getVersion());
        }
    }
}
