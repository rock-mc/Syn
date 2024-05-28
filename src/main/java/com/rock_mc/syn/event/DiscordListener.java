package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
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

        if (cmdLine.equals(CmdManager.SYN) || Utils.matchCommand(cmdLine, "help")) {
            CmdHelp.run(plugin, log, null);

        } else if (Utils.matchCommand(cmdLine, CmdManager.VERIFY)) {
            log.send("請進入遊戲中輸入驗證碼。");

        } else if (Utils.matchCommand(cmdLine, CmdManager.GENCODE)) {
            CmdGenCode.run(plugin, log, null, Utils.extractArgs(CmdManager.GENCODE, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.INFO)) {
            CmdInfo.run(plugin, log, null, Utils.extractArgs(CmdManager.INFO, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.BAN)) {
            CmdBan.run(plugin, log, null, Utils.extractArgs(CmdManager.BAN, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.UNBAN)) {
            CmdUnban.run(plugin, log, null, Utils.extractArgs(CmdManager.UNBAN, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.GUEST)) {
            CmdGuest.run(plugin, log, null, Utils.extractArgs(CmdManager.GUEST, cmdLine));

        } else if (Utils.matchCommand(cmdLine, CmdManager.LOG)) {
            CmdGuest.run(plugin, log, null, Utils.extractArgs(CmdManager.LOG, cmdLine));

        }

    }


}
