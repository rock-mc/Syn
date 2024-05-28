package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.JoinEvent;
import com.rock_mc.syn.event.KickEvent;
import com.rock_mc.syn.log.LogProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CmdVerify {
    private final static String commandName = CmdManager.VERIFY;

    public static void run(Syn plugin, LogProvider log, Player player, String[] args) {

        if (plugin.lacksPermission(player, commandName)) {
            log.sendMessage(player, "You don't have permission to use this command.");
            return;
        }

        if (args.length != 2) {
            log.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
            return;
        }

        if (plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
            log.sendMessage(player, "你已經通過驗證了。");
            return;
        }

        int maxInputCodeTimes = plugin.getConfig().getInt(Config.MAX_INPUT_CODE_TIMES);
        int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());

        if (failTime >= maxInputCodeTimes) {

            int banDays = plugin.getConfig().getInt(Config.INPUT_CODE_BAN_DAYS);
            String message = "請勿亂猜驗證碼，冷靜個 " + banDays + " 天再來吧";
            String banReason = "try code too much times";

            long banedSec = (long) banDays * 24 * 60 * 60;

            plugin.dbManager.addPlayerToBannedList(player.getUniqueId().toString(), banReason, banedSec);
            plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), 1);

            Event event = new KickEvent(false, player, message);
            Bukkit.getPluginManager().callEvent(event);
            return;
        } else {
            plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);
        }

        String code = args[1];
        if (!Utils.isValidCode(plugin.getConfig().getString(Config.AVAILABLE_CHARS), plugin.getConfig().getInt(Config.CODE_LENGTH), code)) {
            log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
            return;
        }

        String codeCreateDate = plugin.dbManager.getCodeCreateDate(code);
        if (codeCreateDate == null) {
            // The verification code is not existed
            log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
            return;
        }
        // check if the code is used or not
        if (plugin.dbManager.isCodeUsed(code)) {
            log.sendMessage(player, ChatColor.RED + "驗證碼已經使用過");
            return;
        }

        // The verification code is exited and not expired
        // Add the player to the allow list
        plugin.dbManager.addPlayerToAllowList(player.getUniqueId().toString());
        // check if the code is expired or not
        int expireDays = plugin.getConfig().getInt(Config.EXPIRE_DAYS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime codeCreatedDateTime = LocalDateTime.parse(codeCreateDate, formatter);

        LocalDateTime currentDateTime = LocalDateTime.now();

        long minesBetween = ChronoUnit.MINUTES.between(codeCreatedDateTime, currentDateTime);

        if (minesBetween > (long) expireDays * 24 * 60) {
            // The verification code is expired
            log.sendMessage(player, ChatColor.RED + "驗證碼過期。");
            return;
        }
        // The verification code is exited and not expired
        // Add the player to the allow list
        plugin.dbManager.addPlayerToAllowList(player.getUniqueId().toString());

        // Mark the verification code as used
        plugin.dbManager.markCode(code, player.getUniqueId().toString());
        plugin.freezePlayerMap.remove(player.getUniqueId());

        Event event = new JoinEvent(false, player, "歡迎 " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " 全新加入!");
        Bukkit.getPluginManager().callEvent(event);

    }
}
