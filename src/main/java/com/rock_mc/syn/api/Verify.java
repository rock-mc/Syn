package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.pluginevent.JoinEvent;
import com.rock_mc.syn.event.pluginevent.KickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Verify {
    private final static String commandName = CmdManager.VERIFY;

    public static boolean exec(Syn plugin, Logger logger, Player player, String code) {

        if (plugin.cmdManager.lacksPermission(player, commandName)) {
            logger.sendMessage(player, "You don't have permission to use this command.");
            return false;
        }

        if (code == null) {
            logger.sendMessage(player, "Invalid code of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
            return false;
        }

        if (plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
            logger.sendMessage(player, "你已經通過驗證了。");
            return false;
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
            return false;
        } else {
            plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);
        }

        if (!Utils.isValidCode(plugin.getConfig().getString(Config.AVAILABLE_CHARS), plugin.getConfig().getInt(Config.CODE_LENGTH), code)) {
            logger.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
            return false;
        }

        String codeCreateDate = plugin.dbManager.getCodeCreateDate(code);
        if (codeCreateDate == null) {
            // The verification code is not existed
            logger.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
            return false;
        }
        // check if the code is used or not
        if (plugin.dbManager.isCodeUsed(code)) {
            logger.sendMessage(player, ChatColor.RED + "驗證碼已經使用過");
            return false;
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
            logger.sendMessage(player, ChatColor.RED + "驗證碼過期。");
            return false;
        }
        // The verification code is exited and not expired
        // Add the player to the allow list
        plugin.dbManager.addPlayerToAllowList(player.getUniqueId().toString());

        // Mark the verification code as used
        plugin.dbManager.markCode(code, player.getUniqueId().toString());
        plugin.freezePlayerMap.remove(player.getUniqueId());

        Event event = new JoinEvent(false, player, "歡迎 " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " 全新加入!");
        Bukkit.getPluginManager().callEvent(event);

        return true;
    }
}
