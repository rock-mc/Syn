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

public class CmdExecutorTest extends PluginTest {


    @Test
    void defaultCmd() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        String expected = Log.PREFIX_GAME;

        String gencode = "* gencode: Generate a the number of verification codes\nUsage: /syn gencode [number]";
        String info = "* info: Show the status of Syn plugin or the player\nUsage: /syn info [player]";
//        String verify = "* verify: The new player input the verification code to verify themselves, or OPs inputs the player's name to verify the Online player\nUsage: /syn verify <code/player>";
        String ban = "* ban: Ban the player\nUsage: /syn ban <player> [day hour min sec]";
        String unban = "* unban: Unban the door\nUsage: /syn unban <player>";
        String open = "* guest: If on, it allows everyone to enter the server, except for players on the ban list. If off, it only allows the player in the allowlist to come into the server\nUsage: /syn guest";
        String close = "* log: Show the log since the time or the last time the server was opened\nUsage: /syn log [time] [player] [page]";

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
        assertEquals("[gencode, info, help, ban, unban, guest, log]", tabList.toString());

        tabList = server.getCommandTabComplete(opPlayer, "syn g");
        assertEquals("[gencode, guest]", tabList.toString());

        PlayerMock player = server.addPlayer();

        tabList = server.getCommandTabComplete(player, "syn ");
        assertEquals("[info, help]", tabList.toString());

    }
}