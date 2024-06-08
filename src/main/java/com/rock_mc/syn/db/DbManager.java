package com.rock_mc.syn.db;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager {

    private final Syn plugin;
    private final Database database;
    private final Object dbLock = new Object();

    private final Map<String, Boolean> codeCache = new HashMap<>();
    private final Map<String, Boolean> playerAllowCache = new HashMap<>();
    private final Map<String, Boolean> playerBannedCache = new HashMap<>();
    private final Map<String, Long> bannedExpireTimeCache = new HashMap<>();
    private final Map<String, String> playerAddCache = new HashMap<>();
    private final Map<String, PluginPlayerInfo> playerInfoCache = new HashMap<>();


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
        synchronized (dbLock) {
            this.database.save();
        }
    }

    public void close() {
        synchronized (dbLock) {
            this.database.close();
        }
    }

    public void addCode(String code) {
        synchronized (dbLock) {
            this.database.addCode(code);
            codeCache.put(code, false);
        }
    }

    public String getCodeCreateDate(String code) {
        synchronized (dbLock) {
            return this.database.getCodeCreateDate(code);
        }
    }

    public boolean containsCode(String code) {
        synchronized (dbLock) {
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
        synchronized (dbLock) {
            this.database.markCode(code, playerUUID);
            codeCache.put(code, true);
        }
    }

    public void addPlayerToAllowList(String playerUUID) {
        synchronized (dbLock) {
            this.database.addAllowedPlayer(playerUUID);
            playerAllowCache.put(playerUUID, true);
        }
    }

    public void removeAllowedPlayer(String playerUUID) {
        synchronized (dbLock) {
            this.database.removeAllowedPlayer(playerUUID);
            playerAllowCache.remove(playerUUID);
        }
    }

    public boolean isPlayerInAllowList(String playerUUID) {
        synchronized (dbLock) {
            if (playerAllowCache.containsKey(playerUUID)) {
                return playerAllowCache.get(playerUUID);
            } else {
                boolean allowed = this.database.isPlayerAllowed(playerUUID);
                playerAllowCache.put(playerUUID, allowed);
                return allowed;
            }
        }
    }

    public boolean isPlayerInBannedList(String playerUUID) {
        synchronized (dbLock) {
            if (playerBannedCache.containsKey(playerUUID)) {
                return playerBannedCache.get(playerUUID);
            } else {
                boolean banned = this.database.isPlayerBanned(playerUUID);
                playerBannedCache.put(playerUUID, banned);
                return banned;
            }
        }
    }

    public void removeCode(String code) {
        synchronized (dbLock) {
            this.database.removeCode(code);
            codeCache.remove(code);
        }
    }

    public int getFailedAttempts(String playerUUID) {
        synchronized (dbLock) {
            return this.database.getFailedAttempts(playerUUID);
        }
    }

    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        synchronized (dbLock) {
            this.database.updateFailedAttempts(playerUUID, failedAttempts);
        }
    }

    public long getBannedExpireTime(String playerUUID) {
        synchronized (dbLock) {
            // 先從緩存中獲取
            Long expireTime = bannedExpireTimeCache.get(playerUUID);
            if (expireTime != null) {
                return expireTime;
            }

            long time = this.database.getBannedExpireTime(playerUUID);

            bannedExpireTimeCache.put(playerUUID, time);

            return time;
        }
    }

    public String getBannedReason(String playerUUID) {
        synchronized (dbLock) {
            return this.database.getBannedReason(playerUUID);
        }
    }

    public String getBannedCreateAt(String playerUUID) {
        synchronized (dbLock) {
            return this.database.getBannedCreateAt(playerUUID);
        }
    }

    public void addPlayerInfo(String playerUUID, String playerName) {
        synchronized (dbLock) {
            if (playerAddCache.containsKey(playerUUID) && playerAddCache.get(playerUUID).equals(playerName)) {
                return;
            }

            this.database.addPlayerInfo(playerUUID, playerName);
            playerAddCache.put(playerUUID, playerName);
        }
    }

    public void addPlayerToBannedList(String playerUUID, String reason, long time) {
        synchronized (dbLock) {
            this.database.addBanedPlayer(playerUUID, reason, time);
            bannedExpireTimeCache.put(playerUUID, time);
            playerBannedCache.put(playerUUID, true);
        }
    }

    public void removePlayerBannedList(String playerUUID) {
        synchronized (dbLock) {
            this.database.removeBanedPlayer(playerUUID);
            bannedExpireTimeCache.remove(playerUUID);
            playerBannedCache.remove(playerUUID);
        }
    }

    public void removePlayerFailedList(String playerUUID) {
        synchronized (dbLock) {
            this.database.removeFailedPlayer(playerUUID);
        }
    }

    public boolean isCodeUsed(String code) {
        synchronized (dbLock) {
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
        synchronized (dbLock) {

            PluginPlayerInfo playerInfo = playerInfoCache.get(playerName);
            if (playerInfo != null) {
                return playerInfo;
            }

            playerInfo = this.database.getPlayerByName(playerName);
            if (playerInfo != null) {
                playerInfoCache.put(playerName, playerInfo);
            }

            return playerInfo;
        }
    }

    public String [] getBannedPlayerList() {
        synchronized (dbLock) {
            return this.database.getBannedPlayerList();
        }
    }

    public void addLogEvent(String playerUUID, String eventName) {
        synchronized (dbLock) {
            this.database.addLogEvent(playerUUID, eventName);
        }
    }

    public List<EventLog> getLogEvents(List<String> playerUUIDs, Timestamp start, Timestamp end) {
        synchronized (dbLock) {
            return this.database.getLogEvents(playerUUIDs, start, end);
        }
    }
}