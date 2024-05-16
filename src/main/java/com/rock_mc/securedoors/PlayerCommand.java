package com.rock_mc.securedoors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (sender instanceof Player player) {

            if (args.length == 0) {
                showDefaultCmd(player);
                return true;
            }

        }

        return true;
    }

    private void showDefaultCmd(Player player) {
        if (player != null) {

            if (player.isOp()) {
                Log.player(player, "verify | gencode | block | unblock | give | list");

            } else if (isVerified(player)) {
                Log.player(player, "gencode");

            } else {
                Log.player(player, "verify <invttation code>");

            }

        }
    }

    private boolean isVerified(Player player) {
        // TODO: see if the player in the white list from database
        return !"guest".equals(player.getName());
    }
}
