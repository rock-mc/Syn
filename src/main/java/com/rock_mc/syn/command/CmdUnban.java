package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.log.LogProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class CmdUnban {
    private final static String commandName = CmdManager.UNBAN;

    public static void run(Syn plugin, LogProvider log, Player player, String[] args) {
        String unblockPlayerName = args[1];
        log.sendMessage(player, "將使用者移出黑名單" + ChatColor.GREEN + unblockPlayerName);


        PlayerInfo unblockPlayer = plugin.dbManager.getPlayerByName(unblockPlayerName);
        if (unblockPlayer == null) {
            log.sendMessage(player, "查無此玩家" + unblockPlayerName);
            return;
        }

        plugin.dbManager.removePlayerBannedList(unblockPlayer.getPlayer_uuid());

        log.sendMessage(player, "執行狀態" + ChatColor.GREEN + "完成");

    }
}
