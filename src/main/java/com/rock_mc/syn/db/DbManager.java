package com.rock_mc.syn.db;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

import java.util.HashMap;
import java.util.Map;

public class DbManager {

    private final Syn plugin;
    private final Database database;
    private final Object lock = new Object();

    private final Map<String, Boolean> codeCache = new HashMap<>();
    private final Map<String, Boolean> playerAllowCache = new HashMap<>();

    public DbManager(Syn plugin) {
        this.plugin = plugin;

        if ("sqlite".equalsIgnoreCase(plugin.configManager.getConfig().getString(Config.DATABASE_TYPE))) {
            this.database = new SQLite(this.plugin);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + plugin.configManager.getConfig().getString(Config.DATABASE_TYPE));
        }
    }

    public void load() {
        this.database.load();
    }

    public void save() {
        synchronized (lock) {
            this.database.save();
        }
    }

    public void close() {
        synchronized (lock) {
            this.database.close();
        }
    }

    public void addCode(String code) {
        synchronized (lock) {
            this.database.addCode(code);
            codeCache.put(code, false);
        }
    }

    public String getCodeCreateDate(String code) {
        synchronized (lock) {
            return this.database.getCodeCreateDate(code);
        }
    }

    public boolean containsCode(String code) {
        synchronized (lock) {
            if (codeCache.containsKey(code)) {
                return codeCache.get(code);
            } else {
                boolean contains = this.database.contains(code);
                codeCache.put(code, contains);
                return contains;
            }
        }
    }

    public void markCode(String code, String playerUUID) {
        synchronized (lock) {
            this.database.markCode(code, playerUUID);
            codeCache.put(code, true);
        }
    }

    public void addPlayerToAllowList(String playerUUID) {
        synchronized (lock) {
            this.database.addAllowedPlayer(playerUUID);
            playerAllowCache.put(playerUUID, true);
        }
    }

    public void removeAllowedPlayer(String playerUUID) {
        synchronized (lock) {
            this.database.removeAllowedPlayer(playerUUID);
            playerAllowCache.remove(playerUUID);
        }
    }

    public boolean isPlayerInAllowList(String playerUUID) {
        synchronized (lock) {
            if (playerAllowCache.containsKey(playerUUID)) {
                return playerAllowCache.get(playerUUID);
            } else {
                boolean allowed = this.database.isPlayerAllowed(playerUUID);
                playerAllowCache.put(playerUUID, allowed);
                return allowed;
            }
        }
    }

    public void removeCode(String code) {
        synchronized (lock) {
            this.database.removeCode(code);
            codeCache.remove(code);
        }
    }

    public int getFailedAttempts(String playerUUID) {
        synchronized (lock) {
            return this.database.getFailedAttempts(playerUUID);
        }
    }

    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        synchronized (lock) {
            this.database.updateFailedAttempts(playerUUID, failedAttempts);
        }
    }

    public long getBannedExpireTime(String playerUUID) {
        synchronized (lock) {
            return this.database.getBannedExpireTime(playerUUID);
        }
    }

    public String getBannedReason(String playerUUID) {
        synchronized (lock) {
            return this.database.getBannedReason(playerUUID);
        }
    }

    public String getBannedCreateAt(String playerUUID) {
        synchronized (lock) {
            return this.database.getBannedCreateAt(playerUUID);
        }
    }

    public void addPlayerInfo(String playerUUID, String playerName) {
        synchronized (lock) {
            this.database.addPlayerInfo(playerUUID, playerName);
        }
    }

    public void addPlayerToBannedList(String playerUUID, String reason, long time) {
        synchronized (lock) {
            this.database.addBanedPlayer(playerUUID, reason, time);
        }
    }

    public void removePlayerBannedList(String playerUUID) {
        synchronized (lock) {
            this.database.removeBanedPlayer(playerUUID);
        }
    }
    public void removePlayerFailedList(String playerUUID) {
        synchronized (lock) {
            this.database.removeFailedPlayer(playerUUID);
        }
    }

    public boolean isCodeUsed(String code) {
        synchronized (lock) {
            if (codeCache.containsKey(code)) {
                return codeCache.get(code);
            } else {
                boolean used = this.database.isCodeUsed(code);
                codeCache.put(code, used);
                return used;
            }
        }
    }
    public PluginPlayerInfo getPlayerByName(String playerName) {
        synchronized (lock) {
            return this.database.getPlayerByName(playerName);
        }
    }
}