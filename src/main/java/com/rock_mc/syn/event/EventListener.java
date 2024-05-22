package com.rock_mc.syn.event;

import com.rock_mc.syn.Log;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
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
    private final Syn plugin;

    public EventListener(Syn plugin) {
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

        String opWelcomeMsg = "管理員 " + ChatColor.GOLD + "" + ChatColor.BOLD + name + ChatColor.WHITE + " 取得女神 " + ChatColor.RED + Syn.APP_NAME + ChatColor.WHITE + " 的允許進入伺服器並得到了女神祝福";
        if (plugin.dbManager.isPlayerAllowed(uuid)) {
            if (player.isOp()) {
                Log.broadcast(opWelcomeMsg);
            } else {
                Log.broadcast("玩家 " + ChatColor.BOLD + name + ChatColor.WHITE + " 取得女神 " + ChatColor.RED + Syn.APP_NAME + ChatColor.WHITE + " 的允許進入伺服器。");
            }
            return;
        }
        else if (player.isOp()) {
            plugin.dbManager.addAllowedPlayer(uuid);
            Log.broadcast(opWelcomeMsg);
            return;
        }

        Log.logInfo("Player " + name + " is not verified, freeze player.");

        Location location = player.getLocation();
        plugin.freezePlayerMap.put(player.getUniqueId(), location);

        new WaitVerify(plugin, player).start();
    }

    @EventHandler
    public void onPluginJoin(JoinEvent event) {
        Log.broadcast(event.getMessage());
        plugin.freezePlayerMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPluginKick(KickEvent event) {
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
