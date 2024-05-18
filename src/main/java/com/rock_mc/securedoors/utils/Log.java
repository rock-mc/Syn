package com.rock_mc.securedoors.utils;

import com.rock_mc.securedoors.SecureDoors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Log {
    public static Logger logger;
    public static final String LOG_PREFIX = "[" + ChatColor.GOLD + SecureDoors.APP_NAME + ChatColor.WHITE + "] ";

    public static String arrayToString(String[] postfix_msg) {
        String result = "";

        if (null == postfix_msg) {
            return result;
        }
        result = String.join(", ", postfix_msg);

        return result;
    }

    public static void player(Player player, String msg) {
        if (player != null) {
            player.sendMessage(LOG_PREFIX + msg);
        }
        else{
            server(msg);
        }
    }

    public static void player(Player player, String prefixMsg, String postfixMsg) {
        player(player, prefixMsg, ChatColor.WHITE, postfixMsg);
    }

    public static void player(Player player, String prefixMsg, ChatColor textColor, String postfixMsg) {
        player(player, prefixMsg + " [" + textColor + postfixMsg + ChatColor.WHITE + "]");
    }

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(LOG_PREFIX + msg);
    }
    public static void broadcast(String prefixMsg, ChatColor chatColor, String postfixMsg) {
        broadcast(prefixMsg + " [" + chatColor + postfixMsg + ChatColor.WHITE + "]");
    }
    public static void broadcast(String prefixMsg, String postfixMsg) {
        broadcast(prefixMsg, ChatColor.WHITE, postfixMsg);
    }

    public static void broadcast(String prefixMsg, String[] postfixMsg) {
        broadcast(prefixMsg, ChatColor.WHITE, arrayToString(postfixMsg));
    }

    public static void server(String msg) {
        logger.info(msg);
    }

    public static void server(String prefixMsg, ChatColor chatColor, String postfixMsg) {
        server(prefixMsg + " [" + chatColor + postfixMsg + ChatColor.WHITE + "]");
    }

    public static void server(String prefixMsg, String postfixMsg) {
        server(prefixMsg, ChatColor.WHITE, postfixMsg);
    }

    public static void server(String prefixMsg, String[] postfixMsg) {
        server(prefixMsg, ChatColor.WHITE, arrayToString(postfixMsg));
    }
}