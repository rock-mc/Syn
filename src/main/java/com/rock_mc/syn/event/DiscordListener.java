package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdGenCode;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.Config;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DiscordListener implements Listener {

    private boolean isDiscordSRVEnabled = false;

    private String CHANNEL_NAME = null;

    private String CHANNEL_ID = null;

    private final Syn plugin;

    public DiscordListener(Syn plugin) {
        this.plugin = plugin;

        Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");

        // Check if DiscordSRV is enabled
        if (discordSRV != null && discordSRV.isEnabled()) {
            isDiscordSRVEnabled = true;

            this.CHANNEL_NAME = plugin.getConfig().getString(Config.CHANNEL_NAME);
            this.CHANNEL_ID = DiscordSRV.getPlugin().getChannels().get(this.CHANNEL_NAME);

            DiscordSRV.api.subscribe(this);
        }
        
    }

    // 接收 discord 訊息
    @Subscribe(priority = ListenerPriority.NORMAL)
    public void onDiscordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(this.CHANNEL_ID)) {
            return;
        }

        String message = event.getMessage().getContentDisplay();
//        String author = event.getAuthor().getName();

        // 產生驗證碼
        if (message.contains(CmdManager.SYN + " " + CmdManager.GENCODE)) {
            String replyMsg = CmdGenCode.run(plugin, 1, false);
            // 回傳訊息
            sendMessageToDiscord(this.CHANNEL_NAME, replyMsg);
        }
    }

    // 傳送 discord 訊息
    public void sendMessageToDiscord(String channelName, String message) {
        if (isDiscordSRVEnabled && channelName != null && message != null) {
            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName)
                    .sendMessage(message).queue();
        }
    }

}
