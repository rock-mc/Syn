package com.rock_mc.syn.log;

import com.rock_mc.syn.Syn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogPlugin implements Log {
    public static final String PREFIX_GAME = "[" + ChatColor.GOLD + Syn.APP_NAME + ChatColor.WHITE + "] ";
    public static final String PREFIX_SERVER = "[" + Syn.APP_NAME + "] ";

    @Override
    public void sendMessage(Player player, String message) {

        if (player != null) {
            player.sendMessage(PREFIX_GAME + message);
        }

        message = ChatColor.stripColor(message);
        sendMessage(message);
    }

    public void broadcast(String message) {
        Bukkit.broadcastMessage(PREFIX_GAME + message);
    }

    // use Bukkit.getConsoleSender() to reply messages on console. the messages could be tested.
    public void sendMessage(String message) { Bukkit.getConsoleSender().sendMessage(PREFIX_SERVER + message); }

    // use Bukkit.getLogger() for different system level logs
    public void logInfo(String message) { Bukkit.getLogger().info(PREFIX_SERVER + message); }

    public void logWarning(String message) {
        Bukkit.getLogger().warning(PREFIX_SERVER + message);
    }

    public void logSevere(String message) {
        Bukkit.getLogger().severe(PREFIX_SERVER + message);
    }
}