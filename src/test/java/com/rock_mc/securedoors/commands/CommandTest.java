package com.rock_mc.securedoors.commands;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.securedoors.Log;
import com.rock_mc.securedoors.PluginTest;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest extends PluginTest {


    @Test
    void defaultCmd() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        opPlayer.setName("opPlayer");

        String expected = Log.PREFIX_GAME + """
Commands:
* gencode: Generate a verification code
Usage: /sd gencode
* info: Show the door information
Usage: /sd info
* ban: Ban the player
Usage: /sd ban <player>
* unban: Unban the door
Usage: /sd unban <player>
* open: Allow everyone to come into the server but the player in the ban list
Usage: /sd open
* close: Allow the player in the allowlist to come into the server
Usage: /sd close""";

        opPlayer.performCommand("sd");

        assertEquals(expected, opPlayer.nextMessage());

        opPlayer.performCommand("sd help");
        assertEquals(expected, opPlayer.nextMessage());

        PlayerMock player = server.addPlayer();
        player.setOp(false);

        expected = Log.PREFIX_GAME + "You don't have permission to use any command.";
        player.performCommand("sd");

        assertEquals(expected, player.nextMessage());

        player.performCommand("sd help");

        assertEquals(expected, player.nextMessage());
    }

    @Test
    void gencodeCmd() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);


        opPlayer.performCommand("sd gencode 3");

        String msgUrl = "https://rock-mc.com/code/?text=";

        String commandOutput = opPlayer.nextMessage();

        assertNotNull(commandOutput);
        assertTrue(commandOutput.contains(msgUrl));

        String code = commandOutput.substring(commandOutput.lastIndexOf(msgUrl) + msgUrl.length());

        PlayerMock newPlayer = server.addPlayer();

        newPlayer.performCommand("sd verify " + code);
        commandOutput = newPlayer.nextMessage();

        assertTrue(commandOutput.contains("歡迎"));

        PlayerMock otherPlayer = server.addPlayer();

        otherPlayer.performCommand("sd verify " + code);
        commandOutput = otherPlayer.nextMessage();

        assertEquals(Log.PREFIX_GAME + ChatColor.RED + "驗證碼已經使用過", commandOutput);
    }


    @Test
    void console() {
        ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
        server.dispatchCommand(consoleSender, "sd");

        assertTrue(consoleSender.nextMessage().contains(Log.PREFIX_SERVER + "Commands:"));

    }
}