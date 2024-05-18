package com.rock_mc.securedoors.commands;

import com.rock_mc.securedoors.utils.Log;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {

    private JavaPlugin plugin;

    public Command(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String commandLabel, String[] args) {

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

        String gencode = "* gencode: Generate a verification code\nUsage: /sd gencode";
        String info = "* info: Show the door information\nUsage: /sd info";
        String verify = "* verify: Verify the verification code\nUsage: /sd verify <verification code>";
        String block = "* block: Block the door\nUsage: /sd block";
        String unblock = "* unblock: Unblock the door\nUsage: /sd unblock";

        String allCommands = "Commands:\n" + gencode + "\n" + info + "\n" + verify + "\n" + block + "\n" + unblock;

        if (player == null) {
            Log.logInfo(allCommands);
        } else {

            String message = "Commands:";

            if (player.hasPermission("sd.gencode")) {
                message += "\n" + gencode;
            }
            if (player.hasPermission("sd.info")) {
                message += "\n" + info;
            }
            if (player.hasPermission("sd.block")) {
                message += "\n" + block;
            }
            if (player.hasPermission("sd.unblock")) {
                message += "\n" + unblock;
            }
            if (message.equals("Commands:")) {
                message = "You don't have permission to use any command.";
            }

            Log.sendMessage(player, message);
        }
    }

    private boolean isVerified(Player player) {
        // TODO: see if the player in the white list from database
        return !"guest".equals(player.getName());
    }
}
