package com.rock_mc.securedoors;

import com.rock_mc.securedoors.config.Config;
import com.rock_mc.securedoors.event.JoinEvent;
import com.rock_mc.securedoors.event.KickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements CommandExecutor, TabCompleter {

    private final SecureDoors plugin;

    public Command(SecureDoors plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String commandLabel, String[] args) {

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        if (args.length == 0 || "help".equals(args[0])) {
            showDefaultCmd(player);
            return true;
        }

        if ("gencode".equals(args[0])) {

            if (player != null && !player.hasPermission(Permission.GENCODE)) {
                Log.sendMessage(player, "You don't have permission to use this command.");
                return true;
            }
            // args[1] = codeNum
            int codeNum = 1;
            if (args.length == 2) {
                try {
                    codeNum = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Log.sendMessage(player, "Usage: /sd gencode [codeNum]");
                    return true;
                }
                if (codeNum < 1) {
                    Log.sendMessage(player, "The codeNum must be greater than 0.");
                    return true;
                }
                if (codeNum > 1000) {
                    Log.sendMessage(player, "The codeNum must be less than 1000.");
                    return true;
                }
            }

            String available_characters = this.plugin.getConfig().getString(Config.AVAILABLE_CHARS);
            int code_length = this.plugin.getConfig().getInt(Config.CODE_LENGTH);

            // Generate a verification code
            // Check the code is unique
            String msg = "";
            for (int i = 0; i < codeNum; i++) {

                String code = Utils.generateCode(available_characters, code_length);
                while (plugin.dbManager.contains(code)) {
                    code = Utils.generateCode(available_characters, code_length);
                }
                plugin.dbManager.addCode(code);


                if (player == null) {
                    msg += "\n" + code;
                } else {
                    String showCodeUrl = this.plugin.getConfig().getString(Config.SHOW_CODE_URL);
                    msg += "\n" + showCodeUrl + code;
                }
            }
            msg = msg.trim();

            Log.sendMessage(player, msg);

            return true;
        }
        if ("verify".equals(args[0])) {

            if (player == null) {
                Log.sendMessage(player, "You must be a player to use this command.");
                return true;
            }

            if (args.length != 2) {
                Log.sendMessage(player, "Usage: /sd verify <verification code>");
                return true;
            }

            int maxInputCodeTimes = this.plugin.getConfig().getInt(Config.MAX_INPUT_CODE_TIMES);
            int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());

            Log.logInfo("failTime: " + failTime + ", maxInputCodeTimes: " + maxInputCodeTimes);
            if (failTime >= maxInputCodeTimes) {

                int banDays = plugin.getConfig().getInt(Config.INPUT_CODE_BAN_DAYS);
                String message = "請勿亂猜驗證碼，冷靜個 " + banDays + " 天再來吧";
                String banReason = "try code too much times";

                long banedSec = (long) banDays * 24 * 60 * 60;

                plugin.dbManager.addBanedPlayer(player.getUniqueId().toString(), banReason, banedSec);
                plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), 1);

                Event event = new KickEvent(false, player, message);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            } else {
                plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);
            }

            String code = args[1];
            if (!Utils.isValidCode(this.plugin.getConfig().getString(Config.AVAILABLE_CHARS), this.plugin.getConfig().getInt(Config.CODE_LENGTH), code)) {
                Log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
                return true;
            }

            String codeCreateDate = plugin.dbManager.getCodeCreateDate(code);
            if (codeCreateDate == null) {
                // The verification code is not existed
                Log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
                return true;
            }
            // check if the code is used or not
            if (plugin.dbManager.isCodeUsed(code)) {
                Log.sendMessage(player, ChatColor.RED + "驗證碼已經使用過");
                return true;
            }

            // The verification code is exited and not expired
            // Add the player to the allow list
            plugin.dbManager.addAllowedPlayer(player.getUniqueId().toString());
            // check if the code is expired or not
            int expireDays = this.plugin.getConfig().getInt(Config.EXPIRE_DAYS);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime codeCreatedDateTime = LocalDateTime.parse(codeCreateDate, formatter);

            LocalDateTime currentDateTime = LocalDateTime.now();

            long minesBetween = ChronoUnit.MINUTES.between(codeCreatedDateTime, currentDateTime);

            if (minesBetween > (long) expireDays * 24 * 60) {
                // The verification code is expired
                Log.sendMessage(player, ChatColor.RED + "驗證碼過期。");
                return true;
            }
            // The verification code is exited and not expired
            // Add the player to the allow list
            plugin.dbManager.addAllowedPlayer(player.getUniqueId().toString());

            // Mark the verification code as used
            plugin.dbManager.markCode(code, true);

            Event event = new JoinEvent(false, player, "歡迎 " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " 全新加入!");
            Bukkit.getPluginManager().callEvent(event);

            return true;
        }

        return true;
    }

    private void showDefaultCmd(Player player) {

        String gencode = "* gencode: Generate a verification code\nUsage: /sd gencode";
        String info = "* info: Show the door information\nUsage: /sd info";
        String verify = "* verify: Verify the verification code\nUsage: /sd verify <verification code>";
        String ban = "* ban: Ban the player\nUsage: /sd ban <player>";
        String unban = "* unban: Unban the door\nUsage: /sd unban <player>";
        String open = "* open: Allow everyone to come into the server but the player in the ban list\nUsage: /sd open";
        String close = "* close: Allow the player in the allowlist to come into the server\nUsage: /sd close";

        String allCommands = "Commands:\n" + gencode + "\n" + info + "\n" + ban + "\n" + unban + "\n" + open + "\n" + close;

        String message;
        if (player == null) {
            message = allCommands;
        } else {

            message = "Commands:";

            if (player.hasPermission(Permission.GENCODE)) {
                message += "\n" + gencode;
            }
            if (player.hasPermission(Permission.INFO)) {
                message += "\n" + info;
            }
            if (player.hasPermission(Permission.BAN)) {
                message += "\n" + ban;
            }
            if (player.hasPermission(Permission.UNBAN)) {
                message += "\n" + unban;
            }
            if (player.hasPermission(Permission.DOOR)) {
                message += "\n" + open + "\n" + close;
            }
            if (message.equals("Commands:")) {
                message = "You don't have permission to use any command.";
            }
        }
        Log.sendMessage(player, message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>(List.of("info", "help", "verify"));

        if (sender.isOp()) {
            tab = new ArrayList<>(List.of("info", "help", "gencode", "ban", "unban", "open", "close"));
        }

        if (args.length == 0) {
            return tab;
        } else if (args.length == 1) {
            return tab.stream().filter(completion -> completion.startsWith(args[args.length - 1])).collect(Collectors.toList());
        }

        return tab;
    }
}
