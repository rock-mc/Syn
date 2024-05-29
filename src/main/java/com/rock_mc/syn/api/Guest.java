package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.log.Log;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Guest {

    private final static String commandName = CmdManager.GUEST;

    public static void run(Syn plugin, Log log, Player player, String[] args) {
        if (plugin.cmdManager.lacksPermission(player, commandName)) {
            log.sendMessage(player, "You don't have permission to use this command.");
            return;
        }

        if (args.length != 1) {
            log.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
            return;
        }

        boolean isGuest = plugin.configManager.getConfig().getBoolean(Config.GUEST);
        isGuest = !isGuest;

        plugin.configManager.getConfig().set(Config.GUEST, isGuest);

        plugin.saveConfig();

        log.sendMessage(player, "訪客模式已經設定為: " + (isGuest ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
        log.sendMessage(player, isGuest ? "所有玩家除了禁止名單都可以進入伺服器。" : "只有在允許名單的玩家可以進入伺服器。");

    }
}
