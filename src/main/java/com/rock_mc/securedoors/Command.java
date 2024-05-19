package com.rock_mc.securedoors;

import com.rock_mc.securedoors.db.DbManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {

    private final JavaPlugin plugin;
    private DbManager dbManager;

    public Command(JavaPlugin plugin, DbManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
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

            // Generate a verification code
            // Check the code is unique

            String code = Utils.generateCode(available_characters, code_length);

            while (this.dbManager.contains(code)) {
                code = Utils.generateCode(available_characters, code_length);
            }
            this.dbManager.addCode(code);

            String msg;
            if (player == null) {
                msg = code;
            } else {
                msg = "https://rock-mc.com/code/?text=" + code;
            }

            Log.sendMessage(player, msg);

            return true;
        }
        if ("verify".equals(args[0])) {

            if (player == null) {
                Log.sendMessage(player, "You must be a player to use this command.");
                return true;
            }

            if (args.length != 2) {
                Log.sendMessage(player, "Usage: /sd verify <verification code>");
                return true;
            }

            String code = args[1];

            if (this.dbManager.contains(code)) {
                this.dbManager.removeCode(code);
                Log.sendMessage(player, "The verification code is correct.");
            } else {
                Log.sendMessage(player, "The verification code is incorrect.");
            }

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

        String allCommands = "Commands:\n" + gencode + "\n" + info + "\n" + ban + "\n" + unban + "\n" + open + "\n" + close;

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

}
