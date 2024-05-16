package com.rock_mc.securedoors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.IOException;
import java.util.UUID;

public class EventListener implements Listener {

    @EventHandler
    public synchronized void onPlayerLogin(PlayerLoginEvent event) throws IOException {
//        final Player player = event.getPlayer();
//        final String name = player.getDisplayName();
//        final UUID uuid = player.getUniqueId();

    }
}
