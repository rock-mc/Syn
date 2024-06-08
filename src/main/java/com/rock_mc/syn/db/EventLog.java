package com.rock_mc.syn.db;

import java.sql.Timestamp;

public class EventLog {
    private final long id;
    private String playerUUID;
    private String eventName;
    private Timestamp createdAt;
    private final String playerName;

    public EventLog(long id, String playerUUID, String eventName, Timestamp createdAt, String playerName) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.eventName = eventName;
        this.createdAt = createdAt;
        this.playerName = playerName;
    }
    public long getId() {
        return id;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getPlayerName() {
        return playerName;
    }
}
