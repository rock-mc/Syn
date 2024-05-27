package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdBan;
import com.rock_mc.syn.command.CmdGenCode;
import com.rock_mc.syn.command.CmdGuest;
import com.rock_mc.syn.command.CmdManager;
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

import java.util.List;

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

        String message = event.getMessage().getContentDisplay();

        if (message.contains(CmdManager.SYN + " " + CmdManager.GENCODE)) {
            CmdGenCode.run(plugin, log, null, extractArgs(CmdManager.GENCODE, message));

        } else if (message.contains(CmdManager.SYN + " " + CmdManager.GUEST)) {
            CmdGuest.run(plugin, log, null, extractArgs(CmdManager.GUEST, message));

        } else if (message.contains(CmdManager.SYN + " " + CmdManager.BAN)) {
            CmdBan.run(plugin, log, null, extractArgs(CmdManager.BAN, message));

        }
    }

    private static @NotNull String[] extractArgs(String cmdCode, String cmdLine) {
        int index = cmdLine.lastIndexOf(CmdManager.SYN + " " + cmdCode);
        if (index == -1) {
            return cmdLine.split(" ");
        }
        cmdLine = cmdLine.substring(index + CmdManager.SYN.length() + 1);
        return cmdLine.split(" ");
    }

    // 傳送 discord 訊息
    public void sendMessageToDiscord(String channelName, String message) {
        if (isDiscordSRVEnabled && channelName != null && message != null) {
            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName)
                    .sendMessage(message).queue();
        }
    }



}
