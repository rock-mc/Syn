package com.rock_mc.syn.db;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DbManager {

    private final Syn plugin;
    private final Database database;
    private final ReadWriteLock dbLock = new ReentrantReadWriteLock();

    private final Map<String, Boolean> codeCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> playerAllowCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> playerBannedCache = new ConcurrentHashMap<>();
    private final Map<String, Long> bannedExpireTimeCache = new ConcurrentHashMap<>();
    private final Map<String, String> playerAddCache = new ConcurrentHashMap<>();
    private final Map<String, PluginPlayerInfo> playerInfoCache = new ConcurrentHashMap<>();
    private volatile String[] bannedPlayerListCache = null;


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
        dbLock.writeLock().lock();
        try {
            this.database.save();
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void close() {
        dbLock.writeLock().lock();
        try {
            this.database.close();
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void clearCaches() {
        codeCache.clear();
        playerAllowCache.clear();
        playerBannedCache.clear();
        bannedExpireTimeCache.clear();
        playerAddCache.clear();
        playerInfoCache.clear();
        bannedPlayerListCache = null;
    }

    public void addCode(String code) {
        dbLock.writeLock().lock();
        try {
            this.database.addCode(code);
            codeCache.put(code, false);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void addCodes(List<String> codes) {
        dbLock.writeLock().lock();
        try {
            this.database.addCodes(codes);
            for (String code : codes) {
                codeCache.put(code, false);
            }
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public String getCodeCreateDate(String code) {
        dbLock.readLock().lock();
        try {
            return this.database.getCodeCreateDate(code);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public boolean containsCode(String code) {
        // Check cache first without lock
        Boolean cached = codeCache.get(code);
        if (cached != null) {
            return cached;
        }
        dbLock.readLock().lock();
        try {
            // Double-check after acquiring lock
            cached = codeCache.get(code);
            if (cached != null) {
                return cached;
            }
            boolean contains = this.database.contains(code);
            codeCache.put(code, contains);
            return contains;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public void markCode(String code, String playerUUID) {
        dbLock.writeLock().lock();
        try {
            this.database.markCode(code, playerUUID);
            codeCache.put(code, true);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void addPlayerToAllowList(String playerUUID) {
        dbLock.writeLock().lock();
        try {
            this.database.addAllowedPlayer(playerUUID);
            playerAllowCache.put(playerUUID, true);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void removeAllowedPlayer(String playerUUID) {
        dbLock.writeLock().lock();
        try {
            this.database.removeAllowedPlayer(playerUUID);
            playerAllowCache.remove(playerUUID);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public boolean isPlayerInAllowList(String playerUUID) {
        Boolean cached = playerAllowCache.get(playerUUID);
        if (cached != null) {
            return cached;
        }
        dbLock.readLock().lock();
        try {
            cached = playerAllowCache.get(playerUUID);
            if (cached != null) {
                return cached;
            }
            boolean allowed = this.database.isPlayerAllowed(playerUUID);
            playerAllowCache.put(playerUUID, allowed);
            return allowed;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public boolean isPlayerInBannedList(String playerUUID) {
        Boolean cached = playerBannedCache.get(playerUUID);
        if (cached != null) {
            return cached;
        }
        dbLock.readLock().lock();
        try {
            cached = playerBannedCache.get(playerUUID);
            if (cached != null) {
                return cached;
            }
            boolean banned = this.database.isPlayerBanned(playerUUID);
            playerBannedCache.put(playerUUID, banned);
            return banned;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public void removeCode(String code) {
        dbLock.writeLock().lock();
        try {
            this.database.removeCode(code);
            codeCache.remove(code);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public int getFailedAttempts(String playerUUID) {
        dbLock.readLock().lock();
        try {
            return this.database.getFailedAttempts(playerUUID);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        dbLock.writeLock().lock();
        try {
            this.database.updateFailedAttempts(playerUUID, failedAttempts);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public long getBannedExpireTime(String playerUUID) {
        Long expireTime = bannedExpireTimeCache.get(playerUUID);
        if (expireTime != null) {
            return expireTime;
        }
        dbLock.readLock().lock();
        try {
            expireTime = bannedExpireTimeCache.get(playerUUID);
            if (expireTime != null) {
                return expireTime;
            }
            long time = this.database.getBannedExpireTime(playerUUID);
            bannedExpireTimeCache.put(playerUUID, time);
            return time;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public String getBannedReason(String playerUUID) {
        dbLock.readLock().lock();
        try {
            return this.database.getBannedReason(playerUUID);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public String getBannedCreateAt(String playerUUID) {
        dbLock.readLock().lock();
        try {
            return this.database.getBannedCreateAt(playerUUID);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public void addPlayerInfo(String playerUUID, String playerName) {
        // Check cache first — skip DB if name hasn't changed
        String cachedName = playerAddCache.get(playerUUID);
        if (playerName.equals(cachedName)) {
            return;
        }
        dbLock.writeLock().lock();
        try {
            this.database.addPlayerInfo(playerUUID, playerName);
            playerAddCache.put(playerUUID, playerName);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void addPlayerToBannedList(String playerUUID, String reason, long time) {
        dbLock.writeLock().lock();
        try {
            this.database.addBannedPlayer(playerUUID, reason, time);
            bannedExpireTimeCache.put(playerUUID, time);
            playerBannedCache.put(playerUUID, true);
            bannedPlayerListCache = null;
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void removePlayerBannedList(String playerUUID) {
        dbLock.writeLock().lock();
        try {
            this.database.removeBannedPlayer(playerUUID);
            bannedExpireTimeCache.remove(playerUUID);
            playerBannedCache.remove(playerUUID);
            bannedPlayerListCache = null;
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public void removePlayerFailedList(String playerUUID) {
        dbLock.writeLock().lock();
        try {
            this.database.removeFailedPlayer(playerUUID);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public boolean isCodeUsed(String code) {
        Boolean cached = codeCache.get(code);
        if (cached != null) {
            return cached;
        }
        dbLock.readLock().lock();
        try {
            cached = codeCache.get(code);
            if (cached != null) {
                return cached;
            }
            boolean used = this.database.isCodeUsed(code);
            codeCache.put(code, used);
            return used;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public PluginPlayerInfo getPlayerByName(String playerName) {
        PluginPlayerInfo playerInfo = playerInfoCache.get(playerName);
        if (playerInfo != null) {
            return playerInfo;
        }
        dbLock.readLock().lock();
        try {
            playerInfo = playerInfoCache.get(playerName);
            if (playerInfo != null) {
                return playerInfo;
            }
            playerInfo = this.database.getPlayerByName(playerName);
            if (playerInfo != null) {
                playerInfoCache.put(playerName, playerInfo);
            }
            return playerInfo;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public String[] getBannedPlayerList() {
        String[] cached = bannedPlayerListCache;
        if (cached != null) {
            return cached;
        }
        dbLock.readLock().lock();
        try {
            cached = bannedPlayerListCache;
            if (cached != null) {
                return cached;
            }
            String[] result = this.database.getBannedPlayerList();
            bannedPlayerListCache = result;
            return result;
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public void addLogEvent(String playerUUID, String eventName) {
        dbLock.writeLock().lock();
        try {
            this.database.addLogEvent(playerUUID, eventName);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public List<EventLog> getLogEvents(List<String> playerUUIDs, Timestamp start, Timestamp end, Integer page, Integer rows) {
        dbLock.readLock().lock();
        try {
            return this.database.getLogEvents(playerUUIDs, start, end, page, rows);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public Long countLogEvent(List<String> playerUUIDs, Timestamp start, Timestamp end) {
        dbLock.readLock().lock();
        try {
            return this.database.countLogEvent(playerUUIDs, start, end);
        } finally {
            dbLock.readLock().unlock();
        }
    }
}
