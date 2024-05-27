package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
import com.rock_mc.syn.event.KickEvent;
import com.rock_mc.syn.log.LogProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


public class CmdBan {

    private final static String commandName = CmdManager.BAN;

    public static void run(Syn plugin, LogProvider log, Player player, String[] args) {
        if (plugin.lacksPermission(player, commandName)) {
            log.sendMessage(player, "You don't have permission to use this command.");
            return;
        }

        String blockPlayerName = args[1];
        log.sendMessage(player, "將使用者加入黑名單" + ChatColor.RED + blockPlayerName);

        int blockDay = 0;
        if (args.length >= 3) {
            blockDay = Integer.parseInt(args[2]);
        }
        int blockHour = 0;
        if (args.length >= 4) {
            blockHour = Integer.parseInt(args[3]);
        }
        int blockMin = 0;
        if (args.length >= 5) {
            blockMin = Integer.parseInt(args[4]);
        }
        int blockSec = 0;
        if (args.length == 6) {
            blockSec = Integer.parseInt(args[5]);
        }

        PlayerInfo playerInfo = plugin.dbManager.getPlayerByName(blockPlayerName);
        if (playerInfo == null) {
            log.sendMessage(player, "查無此玩家" + blockPlayerName);
            return;
        }

        plugin.dbManager.addPlayerToBannedList(playerInfo.getPlayer_uuid(),
                "Admin Ban",
                blockDay * 24 * 60 * 60L + blockHour * 60 * 60L + blockMin * 60L + blockSec);

        plugin.dbManager.removePlayerFailedList(playerInfo.getPlayer_uuid());

        OfflinePlayer blockPlayer = Bukkit.getOfflinePlayer(playerInfo.getUUID());
        if (blockPlayer.isOnline()) {
            // 如果在線上踢掉
            // 順便告訴他刑期，很棒吧
            String blockMsg;
            if (blockDay == 0 && blockHour == 0 && blockMin == 0 && blockSec == 0) {
                blockMsg = "抱歉，你已經被永久加入黑名單";
            } else {
                blockMsg = "抱歉，你已經被加入黑名單，刑期 ";
                blockMsg += Utils.timeToStr(blockDay, blockHour, blockMin, blockSec);
            }

            Player kickPlayer = Bukkit.getPlayer(playerInfo.getUUID());

            Event event = new KickEvent(false, kickPlayer, blockMsg);
            Bukkit.getPluginManager().callEvent(event);
        }

        log.sendMessage(player, "執行狀態" + ChatColor.GREEN + "完成");

    }
}
