package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.pluginevent.JoinEvent;
import com.rock_mc.syn.event.pluginevent.KickEvent;
import com.rock_mc.syn.log.LogManager;
import com.rock_mc.syn.log.LoggerPlugin;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.List;

public class EventListener implements Listener {
    private final Syn plugin;

    private LoggerPlugin LOG_PLUGIN;

    public EventListener(Syn plugin) {
        this.plugin = plugin;

        LOG_PLUGIN = LogManager.LOG_PLUGIN;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) throws IOException {
        final Player player = event.getPlayer();
        final String name = player.getName();
        final String uuid = player.getUniqueId().toString();

        // 進來就建立玩家資料（非同步，不阻塞主執行緒）
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.dbManager.addPlayerInfo(uuid, name));

        if (!plugin.dbManager.isPlayerInBannedList(uuid)) {
            // Player is not banned
            LOG_PLUGIN.logInfo("Player " + name + " is not in banned list.");
            event.allow();
            return;
        }

        // check ban time is expired or not
        long banedSecs = plugin.dbManager.getBannedExpireTime(uuid);
        if (banedSecs == -1) {
            // DB error or inconsistent state — treat as permanent ban
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "抱歉！你是永久禁止名單。");
            LOG_PLUGIN.logWarning("Player " + name + " is in banned list but expire time query failed.");
            return;
        }

        // the player is banned and the ban time is not expired
        // calculate how long is left

        String bannedCreateAtDate = plugin.dbManager.getBannedCreateAt(uuid);

        // convert bannedCreateAtDate = "2024-05-19 08:00:39" to epoch seconds
        long bannedCreateAtSecs = java.time.LocalDateTime.parse(bannedCreateAtDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(java.time.ZoneOffset.UTC);

        long now = java.time.Instant.now().getEpochSecond();
        if (banedSecs != 0 && now > banedSecs + bannedCreateAtSecs) {
            plugin.dbManager.removePlayerBannedList(uuid);
            LOG_PLUGIN.logInfo("Player " + name + " is removed from banned list.");
            event.allow();
            return;
        }

        String kickMsg;
        if (banedSecs == 0) {
            kickMsg = "抱歉！你是永久禁止名單。";
        } else {
            kickMsg = "抱歉！你被列為禁止名單！\n刑期尚有 ";
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

        List<String> welcome = plugin.configManager.getConfig().getStringList(Config.WELCOME);
        String goddessName = plugin.configManager.getConfig().getString(Config.GODDESS_NAME);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.dbManager.addLogEvent(uuid, "login"));

        String opWelcomeMsg = "管理員 " + ChatColor.DARK_RED + "" + ChatColor.BOLD + name + ChatColor.RESET + " 取得" + goddessName + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 的允許進入伺服器並得到了" + goddessName + "祝福";
        if (plugin.dbManager.isPlayerInAllowList(uuid)) {
            if (player.isOp()) {
                event.setJoinMessage(LoggerPlugin.PREFIX_GAME + opWelcomeMsg);
            } else {
                event.setJoinMessage(LoggerPlugin.PREFIX_GAME + "玩家 " + ChatColor.GREEN + "" + ChatColor.BOLD + name + ChatColor.RESET + " 取得" + goddessName + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 的允許進入伺服器。");
            }

            LOG_PLUGIN.sendMessage(player, goddessName + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 輕輕地在你耳邊說：\n" + welcome.get((int) (Math.random() * welcome.size())));
        } else if (player.isOp()) {
            plugin.dbManager.addPlayerToAllowList(uuid);

            event.setJoinMessage(LoggerPlugin.PREFIX_GAME + opWelcomeMsg);
            LOG_PLUGIN.sendMessage(player, goddessName + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 輕輕地在你耳邊說：\n" + welcome.get((int) (Math.random() * welcome.size())));
        } else if (plugin.configManager.getConfig().getBoolean(Config.GUEST)) {
            LOG_PLUGIN.logInfo("Guest mode is enabled");
            event.setJoinMessage(LoggerPlugin.PREFIX_GAME + "訪客玩家 " + ChatColor.BOLD + name + ChatColor.RESET + " 取得" + goddessName + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 的暫時允許進入伺服器。");
        } else {
            LOG_PLUGIN.logInfo("Player " + name + " is not verified, freeze player.");

            Location location = player.getLocation();
            plugin.freezePlayerMap.put(player.getUniqueId(), location);

            // 失明 + 緩速，讓未驗證玩家無法正常遊玩
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));

            new WaitVerify(plugin, player).start();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String uuid = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.dbManager.addLogEvent(uuid, "logout"));
    }

    @EventHandler
    public void onPluginJoin(JoinEvent event) {
        LOG_PLUGIN.broadcast(event.getMessage());
        Player player = event.getPlayer();
        plugin.freezePlayerMap.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOW);
    }

    @EventHandler
    public void onPluginKick(KickEvent event) {
        Player player = event.getPlayer();
        player.kickPlayer(event.getMessage());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location frozenLoc = plugin.freezePlayerMap.get(player.getUniqueId());
        if (frozenLoc == null) {
            return;
        }
        // 只允許轉頭（yaw/pitch），阻止位移和跳躍
        Location to = event.getTo();
        if (to != null) {
            to.setX(frozenLoc.getX());
            to.setY(frozenLoc.getY());
            to.setZ(frozenLoc.getZ());
            event.setTo(to);
        }
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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (!plugin.freezePlayerMap.containsKey(player.getUniqueId())) {
            return;
        }

        // 凍結狀態，取消對話
        event.setCancelled(true);
        LOG_PLUGIN.sendMessage(player, "因為您尚未通過驗證，因此訊息並未送出");
    }
}
