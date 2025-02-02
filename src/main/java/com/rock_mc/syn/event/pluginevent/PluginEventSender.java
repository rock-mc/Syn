package com.rock_mc.syn.event.pluginevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginEventSender {

    public static void sendJoinEvent(Player player, String message) {
        callOnMainThread(new JoinEvent(false, player, message));
    }

    public static void sendKickEvent(Player player, String message) {
        callOnMainThread(new KickEvent(false, player, message));
    }

    // Discord 等非主線程呼叫時，同步事件會丟 IllegalStateException；
    // 改以 async 事件又會讓 kickPlayer、removePotionEffect 在非主線程執行，所以一律切回主線程。
    private static void callOnMainThread(Event event) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(PluginEventSender.class),
                () -> Bukkit.getPluginManager().callEvent(event));
    }
}
