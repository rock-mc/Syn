package com.rock_mc.syn.event.pluginevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PluginEventSender {

    public static void sendJoinEvent(Player player, String message) {
        try {
            Event joinEvent = new JoinEvent(false, player, message);
            Bukkit.getPluginManager().callEvent(joinEvent);
        } catch (java.lang.IllegalStateException e) {
            Event joinEvent = new JoinEvent(true, player, message);
            Bukkit.getPluginManager().callEvent(joinEvent);
        }
    }

    public static void sendKickEvent(Player player, String message) {
        try {
            Event kickEvent = new KickEvent(false, player, message);
            Bukkit.getPluginManager().callEvent(kickEvent);
        } catch (java.lang.IllegalStateException e) {
            Event kickEvent = new KickEvent(true, player, message);
            Bukkit.getPluginManager().callEvent(kickEvent);
        }
    }
}
