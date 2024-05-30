package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Logger;
import org.bukkit.entity.Player;

public class Log {
    private final static String commandName = CmdManager.LOG;

    public static void run(Syn plugin, Logger logger, Player player, String[] args) {
        // TODO: syn log [time] [player] [page]
    }
}
