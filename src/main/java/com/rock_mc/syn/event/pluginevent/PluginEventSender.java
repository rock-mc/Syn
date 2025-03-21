package com.rock_mc.syn.event.pluginevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Objects;

public class PluginEventSender {

    public static void sendJoinEvent(Player player, String message) {
        try {
            Event joinEvent = new JoinEvent(false, player, message);
            Bukkit.getPluginManager().callEvent(joinEvent);
        } catch (java.lang.IllegalStateException e) {

            try {
                Event joinEvent = new JoinEvent(true, player, message);
                Bukkit.getPluginManager().callEvent(joinEvent);
            } catch (java.lang.IllegalStateException e2) {

                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Syn")), () -> {
                    player.sendMessage(message);
                });
            }
        }
    }

    public static void sendKickEvent(Player player, String message) {
        // 確保在主線程執行踢出操作
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Syn")), () -> {
            try {
                Event kickEvent = new KickEvent(false, player, message);
                Bukkit.getPluginManager().callEvent(kickEvent);
            } catch (Exception e) {
                // 如果事件觸發失敗，直接踢出玩家
                player.kickPlayer(message);
            }
        });
    }
}
