package com.rock_mc.syn.db;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerInfo {

    private String player_uuid;
    private String player_name;
    private Timestamp last_login;
    private Timestamp created_at;

    public PlayerInfo(String player_uuid, String player_name, Timestamp last_login, Timestamp created_at) {
        this.player_uuid = player_uuid;
        this.player_name = player_name;
        this.last_login = last_login;
        this.created_at = created_at;
    }

    public UUID getUUID() {
        return UUID.fromString(player_uuid);
    }

    public String getPlayer_uuid() {
        return player_uuid;
    }

    public void setPlayer_uuid(String player_uuid) {
        this.player_uuid = player_uuid;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
