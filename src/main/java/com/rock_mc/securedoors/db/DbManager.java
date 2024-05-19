package com.rock_mc.securedoors.db;

import com.rock_mc.securedoors.SecureDoors;

public class DbManager {

    private final SecureDoors plugin;
    private Database database;

    public DbManager(SecureDoors plugin) {
        this.plugin = plugin;

        if ("sqlite".equalsIgnoreCase(plugin.configManager.getConfig().getString("door.database.type"))) {
            this.database = new SQLite(this.plugin);
        }
    }

    public void load() {
        this.database.load();
    }

    public void save() {
        this.database.save();
    }

    public void close() {
        this.database.close();
    }

    public void addCode(String code) {
        this.database.addCode(code);
    }
    public String getCodeCreateDate(String code) {
        return this.database.getCodeCreateDate(code);
    }
    public boolean contains(String code) {
        return this.database.contains(code);
    }
    public void markCode(String code, boolean used) {
        this.database.markCode(code, used);
    }
    public void addAllowedPlayer(String playerUUID) {
        this.database.addAllowedPlayer(playerUUID);
    }
    public void removeAllowedPlayer(String playerUUID) {
        this.database.removeAllowedPlayer(playerUUID);
    }

    public boolean isPlayerAllowed(String playerUUID) {
        return this.database.isPlayerAllowed(playerUUID);
    }

    public void removeCode(String code) {
        this.database.removeCode(code);
    }

    public int getFailedAttempts(String playerUUID) {
        return this.database.getFailedAttempts(playerUUID);
    }

    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        this.database.updateFailedAttempts(playerUUID, failedAttempts);
    }

    public long getBannedExpireTime(String playerUUID) {
        return this.database.getBannedExpireTime(playerUUID);
    }

    public String getBannedReason(String playerUUID) {
        return this.database.getBannedReason(playerUUID);
    }

    public String getBannedCreateAt(String playerUUID) {
        return this.database.getBannedCreateAt(playerUUID);
    }

    public void addPlayerInfo(String playerUUID, String playerName) {
        this.database.addPlayerInfo(playerUUID, playerName);
    }

    public void addBanedPlayer(String playerUUID, String reason, long time) {
        this.database.addBanedPlayer(playerUUID, reason, time);
    }

    public void removeBanedPlayer(String playerUUID) {
        this.database.removeBanedPlayer(playerUUID);
    }
}
