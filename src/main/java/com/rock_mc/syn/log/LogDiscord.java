package com.rock_mc.syn.log;

import com.rock_mc.syn.event.DiscordListener;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;

public class LogDiscord implements Log {

    public void sendMessage(Player player, String message) {
        send(message);
    }

    public void send(String message) {

        if (message == null) {
            return;
        }
        if (!DiscordListener.isDiscordSRVEnabled) {
            return;
        }

        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordListener.CHANNEL_NAME)
                .sendMessage(message).queue();
    }

}