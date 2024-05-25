
package com.rock_mc.syn.db;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

public class DbManager {

    private final Syn plugin;
    private final Database database;
    private final Object lock = new Object();

    public DbManager(Syn plugin) {
        this.plugin = plugin;

        if ("sqlite".equalsIgnoreCase(plugin.configManager.getConfig().getString(Config.DATABASE_TYPE))) {
            this.database = new SQLite(this.plugin);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + plugin.configManager.getConfig().getString(Config.DATABASE_TYPE));
        }
    }

    public void load() {
        synchronized (lock) {
            this.database.load();
        }
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
        }
    }

    public String getCodeCreateDate(String code) {
        synchronized (lock) {
            return this.database.getCodeCreateDate(code);
        }
    }
    public boolean containsCode(String code) {
        synchronized (lock) {
            return this.database.contains(code);
        }
    }

    public void markCode(String code, String playerUUID) {
        synchronized (lock) {
            this.database.markCode(code, playerUUID);
        }
    }

    public void addPlayerToAllowList(String playerUUID) {
        synchronized (lock) {
            this.database.addAllowedPlayer(playerUUID);
        }
    }

    public void removeAllowedPlayer(String playerUUID) {
        synchronized (lock) {
            this.database.removeAllowedPlayer(playerUUID);
        }
    }

    public boolean isPlayerInAllowList(String playerUUID) {
        synchronized (lock) {
            return this.database.isPlayerAllowed(playerUUID);
        }
    }

    public void removeCode(String code) {
        synchronized (lock) {
            this.database.removeCode(code);
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

    public boolean isCodeUsed(String code) {
        synchronized (lock) {
            return this.database.isCodeUsed(code);
        }
    }
}
