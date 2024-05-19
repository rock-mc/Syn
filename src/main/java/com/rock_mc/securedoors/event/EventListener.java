package com.rock_mc.securedoors.event;

import com.rock_mc.securedoors.Log;
import com.rock_mc.securedoors.SecureDoors;
import com.rock_mc.securedoors.Utils;
import com.rock_mc.securedoors.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;

public class EventListener implements Listener {
    private final SecureDoors plugin;

    public EventListener(SecureDoors plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) throws IOException {
        final Player player = event.getPlayer();
        final String name = player.getDisplayName();
        final String uuid = player.getUniqueId().toString();

        // 進來就建立玩家資料
        plugin.dbManager.addPlayerInfo(uuid, name);

        if (plugin.dbManager.isPlayerAllowed(uuid)) {
            return;
        }

        // get expire_time from db
        long banedSecs = plugin.dbManager.getBannedExpireTime(uuid);
        if (banedSecs == -1) {
            // Player is not banned
            return;
        }

        String bannedCreateAtDate = plugin.dbManager.getBannedCreateAt(uuid);

        // convert bannedCreateAtDate = "2024-05-19 08:00:39" to epoch seconds
        long bannedCreateAtSecs = java.time.LocalDateTime.parse(bannedCreateAtDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(java.time.ZoneOffset.UTC);

        long now = java.time.Instant.now().getEpochSecond();
        if (now > banedSecs + bannedCreateAtSecs) {
            plugin.dbManager.removeBanedPlayer(uuid);
            return;
        }

        String kickMsg;
        if (banedSecs == 0) {
            kickMsg = "抱歉！你是永久黑名單。";
        } else {
            kickMsg = "抱歉！你被列為黑名單！\n刑期尚有 ";
            long expiryTime = banedSecs + bannedCreateAtSecs;
            expiryTime -= now;
            kickMsg += Utils.timeToStr(expiryTime);
        }

        // Kick
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMsg);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        final Player player = event.getPlayer();
        final String name = player.getDisplayName();
        final String uuid = player.getUniqueId().toString();

        if (plugin.dbManager.isPlayerAllowed(uuid)) {
            if (player.isOp()) {
                Log.broadcast("管理員 " + ChatColor.BOLD + "" + ChatColor.GOLD + name + ChatColor.WHITE + " 通過驗證。");
            } else {
                Log.broadcast("玩家 " + ChatColor.BOLD + name + ChatColor.WHITE + " 通過驗證。");
            }
            return;
        }

        if (player.isOp()) {
            plugin.dbManager.addAllowedPlayer(uuid);
            Log.broadcast("管理員 " + ChatColor.BOLD + "" + ChatColor.GOLD + name + ChatColor.WHITE + " 通過驗證。");
            return;
        }
        Log.logInfo("玩家 " + name + " 未通過驗證，凍結玩家。");

        Location location = player.getLocation();
        plugin.freezePlayerMap.put(player.getUniqueId(), location);

        new WaitVerify(plugin, player).start();
    }

    @EventHandler
    public void onSDJoin(JoinEvent event) {
        Log.broadcast(event.getMessage());
        plugin.freezePlayerMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSDKick(KickEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(event.getMessage()));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.freezePlayerMap.containsKey(player.getUniqueId())) {
            return;
        }
        LivingEntity livingEntity = player;
        if (!livingEntity.isOnGround()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (!plugin.freezePlayerMap.containsKey(player.getUniqueId())) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (!plugin.freezePlayerMap.containsKey(player.getUniqueId())) {
                return;
            }
            e.setCancelled(true);
        }
    }
}
