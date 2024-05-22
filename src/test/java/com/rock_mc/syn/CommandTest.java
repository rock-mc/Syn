package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.collect.Lists;
import com.rock_mc.syn.event.WaitVerify;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest extends PluginTest {


    @Test
    void defaultCmd() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        String expected = Log.PREFIX_GAME;

        String gencode = "* gencode: Generate a verification code\nUsage: /syn gencode";
        String info = "* info: Show the door information\nUsage: /syn info";
//        String verify = "* verify: Verify the verification code\nUsage: /syn verify <verification code>";
        String ban = "* ban: Ban the player\nUsage: /syn ban <player>";
        String unban = "* unban: Unban the door\nUsage: /syn unban <player>";
        String open = "* open: Allow everyone to come into the server but the player in the ban list\nUsage: /syn open";
        String close = "* close: Allow the player in the allowlist to come into the server\nUsage: /syn close";

        expected += "Commands:\n" + gencode + "\n" + info + "\n" + ban + "\n" + unban + "\n" + open + "\n" + close;

        opPlayer.performCommand("syn");

        assertEquals(expected, opPlayer.nextMessage());

        opPlayer.performCommand("syn help");
        assertEquals(expected, opPlayer.nextMessage());

        PlayerMock player = server.addPlayer();
        player.setOp(false);

        expected = Log.PREFIX_GAME + "You don't have permission to use any command.";
        player.performCommand("syn");

        assertEquals(expected, player.nextMessage());

        player.performCommand("syn help");

        assertEquals(expected, player.nextMessage());
    }

    @Test
    void gencodeCmd() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);


        opPlayer.performCommand("syn gencode 3");

        String msgUrl = "https://rock-mc.com/code/?text=";

        String commandOutput = opPlayer.nextMessage();

        assertNotNull(commandOutput);
        assertTrue(commandOutput.contains(msgUrl));

        String code = commandOutput.substring(commandOutput.lastIndexOf(msgUrl) + msgUrl.length());

        PlayerMock newPlayer = server.addPlayer();

        newPlayer.performCommand("syn verify " + code);
        commandOutput = newPlayer.nextMessage();


        assert commandOutput != null;
        assertTrue(commandOutput.contains("歡迎"));

        PlayerMock otherPlayer = server.addPlayer();

        otherPlayer.performCommand("syn verify " + code);
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

        player.performCommand("syn verify " + code);

        commandOutput = player.nextMessage();

        assertEquals(Log.PREFIX_GAME + ChatColor.RED + "驗證碼錯誤", commandOutput);
    }

    @Test
    void console() {
        ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
        server.dispatchCommand(consoleSender, "syn");

        String commandOutput = consoleSender.nextMessage();

        assert commandOutput != null;
        assertTrue(commandOutput.contains(Log.PREFIX_SERVER + "Commands:"));

    }

    @Test
    void tabComplete() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        List<String> tabList = Lists.newArrayList();

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        tabList = server.getCommandTabComplete(opPlayer, "syn ");
        assertEquals("[info, help, gencode, ban, unban, open, close]", tabList.toString());

        tabList = server.getCommandTabComplete(opPlayer, "syn g");
        assertEquals("[gencode]", tabList.toString());

        PlayerMock player = server.addPlayer();

        tabList = server.getCommandTabComplete(player, "syn ");
        assertEquals("[info, help, verify]", tabList.toString());

    }
}