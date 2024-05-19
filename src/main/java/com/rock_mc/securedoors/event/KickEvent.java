package com.rock_mc.securedoors.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KickEvent extends Event {

    private Player player;
    private String msg;

    public KickEvent(boolean isAsync, Player kickPlayer, String display_msg) {
        super(isAsync);
        player = kickPlayer;
        msg = display_msg;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return msg;
    }
}

