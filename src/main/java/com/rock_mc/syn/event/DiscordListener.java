package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.api.*;
import com.rock_mc.syn.log.LogManager;
import com.rock_mc.syn.utlis.Utils;
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

public class DiscordListener implements Listener {

    public static boolean isDiscordSRVEnabled = false;

    public static String CHANNEL_NAME = null;

    private final Syn plugin;

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

        LogDiscord Log = LogManager.LOG_DISCORD;

        String cmdLine = event.getMessage().getContentDisplay().trim();

        if (cmdLine.equals(CmdManager.SYN) || Utils.matchCommand(cmdLine, "help")) {
            Help.run(plugin, Log, null);

        } else if (Utils.matchCommand(cmdLine, CmdManager.VERIFY)) {
            Log.send("請進入遊戲中輸入驗證碼。");

        } else if (Utils.matchCommand(cmdLine, CmdManager.GENCODE)) {
            GenCode.run(plugin, Log, null, Utils.extractArgs(CmdManager.GENCODE, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.INFO)) {
            Info.run(plugin, Log, null, Utils.extractArgs(CmdManager.INFO, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.BAN)) {
            Ban.run(plugin, Log, null, Utils.extractArgs(CmdManager.BAN, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.UNBAN)) {
            Unban.run(plugin, Log, null, Utils.extractArgs(CmdManager.UNBAN, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.GUEST)) {
            Guest.run(plugin, Log, null, Utils.extractArgs(CmdManager.GUEST, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.LOG)) {
            Guest.run(plugin, Log, null, Utils.extractArgs(CmdManager.LOG, cmdLine));
        }
    }
}
