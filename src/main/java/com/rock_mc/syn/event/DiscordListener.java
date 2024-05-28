package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.*;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.log.LogDiscord;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DiscordListener implements Listener {

    public static boolean isDiscordSRVEnabled = false;

    public static String CHANNEL_NAME = null;

    private final Syn plugin;

    private final static LogDiscord log = new LogDiscord();

    public DiscordListener(Syn plugin) {
        this.plugin = plugin;

        Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");

        // Check if DiscordSRV is enabled
        if (discordSRV != null && discordSRV.isEnabled()) {
            isDiscordSRVEnabled = true;
            CHANNEL_NAME = plugin.getConfig().getString(Config.CHANNEL_NAME);

            DiscordSRV.api.subscribe(this);
        }
    }

    // 接收 discord 訊息
    @Subscribe(priority = ListenerPriority.NORMAL)
    public void onDiscordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        String channelId = DiscordSRV.getPlugin().getChannels().get(CHANNEL_NAME);

        if (!event.getChannel().getId().equals(channelId)) {
            return;
        }

        String cmdLine = event.getMessage().getContentDisplay().trim();

        if (cmdLine.equals(CmdManager.SYN) || matchCommand(cmdLine, "help")) {
            CmdHelp.run(plugin, log, null);

        } else if (matchCommand(cmdLine, CmdManager.VERIFY)) {
            log.send("請進入遊戲中輸入驗證碼。");

        } else if (matchCommand(cmdLine, CmdManager.GENCODE)) {
            CmdGenCode.run(plugin, log, null, extractArgs(CmdManager.GENCODE, cmdLine));

        } else if (matchCommand(cmdLine, CmdManager.INFO)) {
            CmdInfo.run(plugin, log, null, extractArgs(CmdManager.INFO, cmdLine));

        } else if (matchCommand(cmdLine, CmdManager.BAN)) {
            CmdBan.run(plugin, log, null, extractArgs(CmdManager.BAN, cmdLine));

        } else if (matchCommand(cmdLine, CmdManager.UNBAN)) {
            CmdUnban.run(plugin, log, null, extractArgs(CmdManager.UNBAN, cmdLine));

        } else if (matchCommand(cmdLine, CmdManager.GUEST)) {
            CmdGuest.run(plugin, log, null, extractArgs(CmdManager.GUEST, cmdLine));

        } else if (matchCommand(cmdLine, CmdManager.LOG)) {
            CmdGuest.run(plugin, log, null, extractArgs(CmdManager.LOG, cmdLine));

        }

    }

    private boolean matchCommand(String commandline, String commandCode) {
        if (commandline == null || commandCode == null) {
            return false;
        }
        return (commandline.contains(CmdManager.SYN + " " + commandCode));
    }

    private static @NotNull String[] extractArgs(String cmdCode, String cmdLine) {
        int index = cmdLine.lastIndexOf(CmdManager.SYN + " " + cmdCode);
        if (index == -1) {
            return cmdLine.split(" ");
        }
        cmdLine = cmdLine.substring(index + CmdManager.SYN.length() + 1);
        return cmdLine.split(" ");
    }

}
