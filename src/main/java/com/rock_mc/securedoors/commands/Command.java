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

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        if (args.length == 0 || "help".equals(args[0])) {
            showDefaultCmd(player);
            return true;
        }

        if ("gencode".equals(args[0])) {

            if (player != null && !player.hasPermission("sd.gencode")) {
                Log.sendMessage(player, "You don't have permission to use this command.");
                return true;
            }

            String available_characters = this.plugin.getConfig().getString("door.available_characters");
            int code_length = this.plugin.getConfig().getInt("door.code_length");

            StringBuilder verification_code = new StringBuilder();
            for (int i = 0; i < code_length; i++) {
                int randomIndex = (int) (Math.random() * available_characters.length());
                char randomChar = available_characters.charAt(randomIndex);

                verification_code.append(randomChar);
            }

            String msg;
            if (player == null) {
                msg = verification_code.toString();
            } else {
                msg = "https://rock-mc.com/code/?text=" + verification_code.toString();
            }

            Log.sendMessage(player, msg);

            return true;
        }


        return true;
    }

    private void showDefaultCmd(Player player) {

        String gencode = "* gencode: Generate a verification code\nUsage: /sd gencode";
        String info = "* info: Show the door information\nUsage: /sd info";
        String verify = "* verify: Verify the verification code\nUsage: /sd verify <verification code>";
        String ban = "* ban: Ban the player\nUsage: /sd ban <player>";
        String unban = "* unban: Unban the door\nUsage: /sd unban <player>";
        String open = "* open: Allow everyone to come into the server but the player in the ban list\nUsage: /sd open";
        String close = "* close: Allow the player in the allowlist to come into the server\nUsage: /sd close";

        String allCommands = "Commands:\n" + gencode + "\n" + info + "\n" + verify + "\n" + ban + "\n" + unban + "\n" + open + "\n" + close;

        String message;
        if (player == null) {
            message = allCommands;
        } else {

            message = "Commands:";

            if (player.hasPermission("sd.gencode")) {
                message += "\n" + gencode;
            }
            if (player.hasPermission("sd.info")) {
                message += "\n" + info;
            }
            if (player.hasPermission("sd.ban")) {
                message += "\n" + ban;
            }
            if (player.hasPermission("sd.unban")) {
                message += "\n" + unban;
            }
            if (player.hasPermission("sd.door")) {
                message += "\n" + open + "\n" + close;
            }
            if (message.equals("Commands:")) {
                message = "You don't have permission to use any command.";
            }
        }
        Log.sendMessage(player, message);
    }

    private boolean isVerified(Player player) {
        // TODO: check if the player in the white list from database
        return !"guest".equals(player.getName());
    }
}
