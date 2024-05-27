package com.rock_mc.syn.command;

import com.google.common.collect.Lists;
import com.rock_mc.syn.Log;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CmdGuest {
    public static @NotNull List<String> run(Syn plugin) {
        List<String> messages = Lists.newArrayList();

        boolean isGuest = plugin.configManager.getConfig().getBoolean(Config.GUEST);
        isGuest = !isGuest;

        plugin.configManager.getConfig().set(Config.GUEST, isGuest);

        plugin.saveConfig();

        messages.add("訪客模式已經設定為: " + (isGuest ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
        messages.add(isGuest ? "所有玩家除了禁止名單都可以進入伺服器。" : "只有在允許名單的玩家可以進入伺服器。");

        return messages;
    }
}
