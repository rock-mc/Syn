package com.rock_mc.syn.log;

import com.rock_mc.syn.event.DiscordListener;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;

public class LogDiscord implements LogProvider {

    public void sendMessage(Player player, String message) {
        send(message);
    }

    public void send(String message) {
        if (DiscordListener.isDiscordSRVEnabled && message != null) {
            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordListener.CHANNEL_NAME)
                    .sendMessage(message).queue();
        }
    }

}