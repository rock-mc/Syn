package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.syn.event.WaitVerify;
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

        String expected = Log.PREFIX_GAME;

        String gencode = "* gencode: Generate a verification code\nUsage: /sd gencode";
        String info = "* info: Show the door information\nUsage: /sd info";
//        String verify = "* verify: Verify the verification code\nUsage: /sd verify <verification code>";
        String ban = "* ban: Ban the player\nUsage: /sd ban <player>";
        String unban = "* unban: Unban the door\nUsage: /sd unban <player>";
        String open = "* open: Allow everyone to come into the server but the player in the ban list\nUsage: /sd open";
        String close = "* close: Allow the player in the allowlist to come into the server\nUsage: /sd close";

        expected += "Commands:\n" + gencode + "\n" + info + "\n" + ban + "\n" + unban + "\n" + open + "\n" + close;

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


        assert commandOutput != null;
        assertTrue(commandOutput.contains("歡迎"));

        PlayerMock otherPlayer = server.addPlayer();

        otherPlayer.performCommand("sd verify " + code);
        commandOutput = otherPlayer.nextMessage();

        assertEquals(Log.PREFIX_GAME + ChatColor.RED + "驗證碼已經使用過", commandOutput);
    }

    @Test
    void verifyCmd() throws InterruptedException {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        int timeoutSeconds = 10;

        String commandOutput;

        PlayerMock player = server.addPlayer();

        new WaitVerify(plugin, player).start();

        // 等待 WaitVerify 執行
        while ((commandOutput = player.nextMessage()) == null && timeoutSeconds > 0) {
            Thread.sleep(1000);
            timeoutSeconds--;
        }
        assertEquals(Log.PREFIX_GAME + "請在 60 秒內輸入驗證碼", commandOutput);

        // 等待使用者輸入
        timeoutSeconds = 10;
        while ((player.nextMessage()) != null && timeoutSeconds > 0) {
            Thread.sleep(1000);
            timeoutSeconds--;
        }

        String code = "123";

        player.performCommand("sd verify " + code);

        commandOutput = player.nextMessage();

        assertEquals(Log.PREFIX_GAME + ChatColor.RED + "驗證碼錯誤", commandOutput);
    }

    @Test
    void console() {
        ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
        server.dispatchCommand(consoleSender, "sd");

        String commandOutput = consoleSender.nextMessage();

        assert commandOutput != null;
        assertTrue(commandOutput.contains(Log.PREFIX_SERVER + "Commands:"));

    }
}