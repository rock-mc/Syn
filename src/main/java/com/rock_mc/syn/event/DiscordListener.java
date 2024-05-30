package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.api.*;
import com.rock_mc.syn.log.LogManager;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import com.rock_mc.syn.command.*;
import com.rock_mc.syn.config.Config;
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

        Logger logger = LogManager.LOG_DISCORD;

        String cmdLine = event.getMessage().getContentDisplay().trim();

        if (!cmdLine.startsWith(CmdManager.SYN)) {
            return;
        }
        cmdLine = cmdLine.substring(CmdManager.SYN.length()).trim();

        String commandName = Utils.extractCommand(plugin.cmdManager.getCmdList(), cmdLine);
        String[] args = cmdLine.split(" ");

        switch (commandName) {
            case "help", "":
                plugin.apiManager.help(logger, null);
                break;
            case CmdManager.VERIFY:
                plugin.apiManager.verify(logger, null, args);
                break;
            case CmdManager.GENCODE:
                String[] codes = plugin.apiManager.genCode(logger, null, args);

                if (codes == null) {
                    return;
                }

                for (String code : codes) {
                    logger.sendMessage(null, code);
                }
                break;
            case CmdManager.GUEST:
                plugin.apiManager.guest(logger, null);
                break;
            case CmdManager.BAN:
                plugin.apiManager.ban(logger, null, args);
                break;
            case CmdManager.UNBAN:
                plugin.apiManager.unban(logger, null, args);
                break;
            case CmdManager.INFO:
                plugin.apiManager.info(logger, null, args);
                break;
            default:
                logger.sendMessage(null, "Invalid command.");
                break;
        }
    }
}
