package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.log.Log;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdExecutor implements CommandExecutor, TabCompleter {
    private final Syn plugin;

    private static final Log log = new Log();

    public CmdExecutor(Syn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String commandLabel, String[] args) {

        Player player = null;
        if (sender instanceof Player tempPlayer) {
            player = tempPlayer;
        }

        String commandName = (args.length == 0) ? "help" : args[0];

        if ("help".equals(commandName)) {
            CmdHelp.run(plugin, log, player);

        } else if (CmdManager.VERIFY.equals(commandName)) {
            CmdVerify.run(plugin, log, player, args);

        } else if (CmdManager.GENCODE.equals(commandName)) {
            CmdGenCode.run(plugin, log, player, args);

        } else if (CmdManager.INFO.equals(commandName)) {
            CmdInfo.run(plugin, log, player, args);

        } else if (CmdManager.GUEST.equals(commandName)) {
            CmdGuest.run(plugin, log, player, args);

        } else if (CmdManager.BAN.equals(commandName)) {
            CmdBan.run(plugin, log, player, args);

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
            }
            else if (CmdManager.VERIFY.equals(args[0])) {
                tab = new ArrayList<>(List.of("code"));
            }
            else if (CmdManager.GUEST.equals(args[0])) {
                tab = new ArrayList<>();
            }
            // TODO: Add more tab complete
        }

        return tab;
    }
}