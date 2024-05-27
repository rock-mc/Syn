package com.rock_mc.syn.command;

import com.rock_mc.syn.log.Log;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.JoinEvent;
import com.rock_mc.syn.event.KickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

public class CmdExecutor implements CommandExecutor, TabCompleter {
    private final Syn plugin;

    private static final Log log = new Log();

    public CmdExecutor(Syn plugin) {
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

        String commandName = args[0];

        if (CmdManager.GENCODE.equals(commandName)) {
            CmdGenCode.run(plugin, log, player, args);
            return true;

        } else if (CmdManager.GUEST.equals(commandName)) {
            CmdGuest.run(plugin, log, player, args);
            return true;

        } else if (CmdManager.BAN.equals(commandName)) {
            CmdBan.run(plugin, log, player, args);
            return true;

        } else if (CmdManager.VERIFY.equals(commandName)) {

            if (plugin.lacksPermission(player, commandName)) {
                log.sendMessage(player, "You don't have permission to use this command.");
                return true;
            }

            if (args.length != 2) {
                log.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
                return true;
            }

            if (plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
                log.sendMessage(player, "你已經通過驗證了。");
                return true;
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
                return true;
            } else {
                plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);
            }

            String code = args[1];
            if (!Utils.isValidCode(plugin.getConfig().getString(Config.AVAILABLE_CHARS), plugin.getConfig().getInt(Config.CODE_LENGTH), code)) {
                log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
                return true;
            }

            String codeCreateDate = plugin.dbManager.getCodeCreateDate(code);
            if (codeCreateDate == null) {
                // The verification code is not existed
                log.sendMessage(player, ChatColor.RED + "驗證碼錯誤");
                return true;
            }
            // check if the code is used or not
            if (plugin.dbManager.isCodeUsed(code)) {
                log.sendMessage(player, ChatColor.RED + "驗證碼已經使用過");
                return true;
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
                return true;
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

        return true;
    }



    private void showDefaultCmd(Player player) {

        String[] allCmds = plugin.cmdManager.getCmdList();

        String allCommands = "Commands:\n";

        for (String cmd : allCmds) {
            allCommands += plugin.cmdManager.getCmd(cmd).description + "\n";
            allCommands += plugin.cmdManager.getCmd(cmd).usage + "\n";
        }
        allCommands = allCommands.trim();

        String message;
        if (player == null) {
            message = allCommands;
        } else if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
            message = "Commands:\n" + plugin.cmdManager.getCmd(CmdManager.VERIFY).description;
            message += "\n" + plugin.cmdManager.getCmd(CmdManager.VERIFY).usage;

        } else {
            message = "Commands:";

            for (String cmd : allCmds) {
                if (plugin.cmdManager.getCmd(cmd).permission != null && !player.hasPermission(plugin.cmdManager.getCmd(cmd).permission)) {
                    continue;
                }
                message += "\n" + plugin.cmdManager.getCmd(cmd).description;
                message += "\n" + plugin.cmdManager.getCmd(cmd).usage;
            }

            if (message.equals("Commands:")) {
                message = "You don't have permission to use any command.";
            }
        }
        log.sendMessage(player, message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        List<String> tab = null;

        String[] allCmds = plugin.cmdManager.getCmdList();

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {

            tab = new ArrayList<>(List.of(CmdManager.VERIFY));

        } else if (args.length == 1) {
            tab = new ArrayList<>();
            for (String cmd : allCmds) {
                if (plugin.cmdManager.getCmd(cmd).permission != null && !player.hasPermission(plugin.cmdManager.getCmd(cmd).permission)) {
                    continue;
                }
                if (cmd.startsWith(args[0])) {
                    tab.add(cmd);
                }
            }
            return tab;
        } else if (args.length == 2) {
            if (CmdManager.GENCODE.equals(args[0])) {
                tab = new ArrayList<>(List.of("1", "3", "5"));
            }
            else if (CmdManager.VERIFY.equals(args[0])) {
                tab = new ArrayList<>(List.of("code"));
            }
            else if (CmdManager.GUEST.equals(args[0])) {
                tab = new ArrayList<>();
            }
            // TODO: Add more tab complete
        }

        return tab;
    }
}