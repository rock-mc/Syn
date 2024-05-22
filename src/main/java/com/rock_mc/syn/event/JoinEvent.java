package com.rock_mc.syn.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JoinEvent extends Event {

    private Player player;
    private String msg;

    public JoinEvent(boolean isAsync, Player joinPlayer, String display_msg) {
        super(isAsync);
        player = joinPlayer;
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

