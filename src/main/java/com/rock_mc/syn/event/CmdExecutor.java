package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.api.*;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.log.LogManager;
import com.rock_mc.syn.log.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CmdExecutor implements CommandExecutor, TabCompleter {
    private final Syn plugin;

    public CmdExecutor(Syn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        String commandName = (args.length == 0) ? "help" : args[0];
        Logger logger = LogManager.LOG_PLUGIN;

        switch (commandName) {
            case "help", "":
                Help.exec(plugin, logger, player);
                break;
            case CmdManager.VERIFY:
                Verify.exec(plugin, logger, player, args);
                break;
            case CmdManager.GENCODE:

                String [] codes = GenCode.exec(plugin, logger, player, args);

                if (codes == null) {
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                String codeURL = (player != null ? plugin.getConfig().getString(Config.SHOW_CODE_URL) : "");

                for (String code : codes) {
                    if (player == null) {
                        if (!stringBuilder.isEmpty()) {
                            stringBuilder.append(", ");
                        }
                        stringBuilder.append(code);
                    } else {
                        stringBuilder.append("\n");
                        stringBuilder.append(codeURL);
                        stringBuilder.append(code);
                    }
                }

                logger.sendMessage(player, stringBuilder.toString().trim());
                break;
            case CmdManager.GUEST:
                Guest.exec(plugin, logger, player);
                break;
            case CmdManager.BAN:
                Ban.exec(plugin, logger, player, args);
                break;
            case CmdManager.UNBAN:
                Unban.exec(plugin, logger, player, args);
                break;
            case CmdManager.INFO:
                Info.exec(plugin, logger, player, args);
                break;
            default:
                logger.sendMessage(player, "Invalid command.");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        List<String> tab = null;

        String[] allCmds = plugin.cmdManager.getCmdList();

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {

            tab = new ArrayList<>(List.of(CmdManager.VERIFY));

        } else if (args.length == 1) {
            tab = new ArrayList<>();
            for (String cmd : allCmds) {
                if (plugin.cmdManager.getCmd(cmd).permission != null && !player.hasPermission(plugin.cmdManager.getCmd(cmd).permission)) {
                    continue;
                }
                if (cmd.startsWith(args[0])) {
                    tab.add(cmd);
                }
            }
            return tab;
        } else if (args.length == 2) {
            if (CmdManager.GENCODE.equals(args[0])) {
                tab = new ArrayList<>(List.of("1", "3", "5"));
            } else if (CmdManager.VERIFY.equals(args[0])) {
                tab = new ArrayList<>(List.of("code"));
            } else if (CmdManager.GUEST.equals(args[0])) {
                tab = new ArrayList<>();
            }
            // TODO: Add more tab complete
        }

        return tab;
    }
}