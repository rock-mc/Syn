package com.rock_mc.syn.log;

import org.bukkit.entity.Player;

public interface LogProvider {
    void sendMessage(Player player, String message);
}
