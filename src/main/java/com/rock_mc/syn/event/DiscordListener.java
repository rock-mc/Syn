package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DiscordListener implements Listener {

    private boolean isDiscordSRVEnabled = false;
    
    private final Syn plugin;

    public DiscordListener(Syn plugin) {
        this.plugin = plugin;

        Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");

        // Check if DiscordSRV is enabled
        if (discordSRV != null && discordSRV.isEnabled()) {
            isDiscordSRVEnabled = true;
            DiscordSRV.api.subscribe(this);
        }
        
    }

    // 接收 discord 訊息
    @Subscribe(priority = ListenerPriority.NORMAL)
    public void onDiscordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        String author = event.getAuthor().getName();

        // TODO: 確認發訊息的身份、產生驗證碼


        // 回傳訊息 DiscordSRV config.yml : 例、Channels: {"syn": "1244127032003137576"}
        sendMessageToDiscord("syn", author + ": " + message);
    }

    // 傳送 discord 訊息
    public void sendMessageToDiscord(String channelName, String message) {
        if (isDiscordSRVEnabled) {
            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName)
                    .sendMessage(message).queue();
        }
    }

}
