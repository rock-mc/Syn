package com.rock_mc.syn.db;


public abstract class Database {

    public abstract void load();

    public abstract void addBannedPlayer(String playerUUID, String reason, long expireTime);

    public abstract String getCodeCreateDate(String code);

    public abstract boolean contains(String code);

    public abstract void markCode(String code, String playerUUID);

    public abstract void addAllowedPlayer(String playerUUID);

    public abstract void removeAllowedPlayer(String playerUUID);

    public abstract boolean isPlayerAllowed(String playerUUID);

    public abstract boolean isPlayerBanned(String playerUUID);

    public abstract void removeCode(String code);

    public abstract int getFailedAttempts(String playerUUID);

    public abstract long getBannedExpireTime(String playerUUID);

    public abstract void removeBannedPlayer(String playerUUID);

    public abstract void removeFailedPlayer(String playerUUID);

    public abstract String getBannedReason(String playerUUID);

    public abstract String getBannedCreateAt(String playerUUID);

    public abstract boolean isCodeUsed(String code);

    public abstract void save();

    public abstract void close();

    public abstract void addCode(String code);

    public abstract void addPlayerInfo(String playerUUID, String playerName);

    public abstract void updateFailedAttempts(String playerUUID, int failedAttempts);

    public abstract PluginPlayerInfo getPlayerByName(String playerName);

    public abstract String [] getBannedPlayerList();
}
