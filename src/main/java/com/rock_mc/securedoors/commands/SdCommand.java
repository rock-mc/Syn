package com.rock_mc.securedoors.commands;

import com.rock_mc.securedoors.utils.Log;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SdCommand implements CommandExecutor {

    private JavaPlugin plugin;

    public SdCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (sender instanceof Player player) {

            if (args.length == 0 || "help".equals(args[0])) {
                showDefaultCmd(player);
                return true;
            }

        } else {
            showDefaultCmd(null);
        }

        return true;
    }

    private void showDefaultCmd(Player player) {

        String allCommands = "verify | gencode | block | unblock | give | list";

        if (player == null) {
            Log.player(null, allCommands);
        } else {

            if (player.isOp()) {
                Log.player(player, allCommands);

            } else if (isVerified(player)) {
                Log.player(player, "gencode");

            } else {
                Log.player(player, "verify <invitation code>");

            }

        }
    }

    private boolean isVerified(Player player) {
        // TODO: see if the player in the white list from database
        return !"guest".equals(player.getName());
    }
}
