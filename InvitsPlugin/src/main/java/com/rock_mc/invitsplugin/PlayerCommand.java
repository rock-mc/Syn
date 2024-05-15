package com.rock_mc.invitsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        Log.server("recv cmd", command.getName());
        Log.server("recv args", args);

        Log.player((Player) sender, commandLabel);

        return true;
    }
}
