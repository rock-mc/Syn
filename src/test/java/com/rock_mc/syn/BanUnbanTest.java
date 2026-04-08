package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BanUnbanTest extends PluginTest {

    @Test
    void banPlayer() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        // Create a target player
        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());

        // Ban the player
        opPlayer.performCommand("syn ban " + targetPlayer.getName());

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("將使用者加入禁止名單"));

        // Verify the player is in the banned list
        assertTrue(plugin.dbManager.isPlayerInBannedList(targetPlayer.getUniqueId().toString()));
    }

    @Test
    void banPlayerWithTime() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());

        // Ban with time
        opPlayer.performCommand("syn ban " + targetPlayer.getName() + " 1d");

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("將使用者加入禁止名單"));

        assertTrue(plugin.dbManager.isPlayerInBannedList(targetPlayer.getUniqueId().toString()));
        assertEquals(86400, plugin.dbManager.getBannedExpireTime(targetPlayer.getUniqueId().toString()));
    }

    @Test
    void unbanPlayer() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());

        // Ban first
        plugin.dbManager.addPlayerToBannedList(targetPlayer.getUniqueId().toString(), "test", 0);
        assertTrue(plugin.dbManager.isPlayerInBannedList(targetPlayer.getUniqueId().toString()));

        // Unban
        opPlayer.performCommand("syn unban " + targetPlayer.getName());

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("將使用者移出禁止名單"));

        assertFalse(plugin.dbManager.isPlayerInBannedList(targetPlayer.getUniqueId().toString()));
    }

    @Test
    void unbanNonBannedPlayer() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());

        opPlayer.performCommand("syn unban " + targetPlayer.getName());

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("使用者不在禁止名單中"));
    }

    @Test
    void unbanTabComplete() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        // Create and ban a player
        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());
        plugin.dbManager.addPlayerToBannedList(targetPlayer.getUniqueId().toString(), "test", 0);

        // Tab complete should show banned player
        List<String> tabList = server.getCommandTabComplete(opPlayer, "syn unban ");
        assertTrue(tabList.contains(targetPlayer.getName()));

        // Unban the player
        plugin.dbManager.removePlayerBannedList(targetPlayer.getUniqueId().toString());

        // Tab complete should no longer show the player (cache invalidated)
        tabList = server.getCommandTabComplete(opPlayer, "syn unban ");
        assertFalse(tabList.contains(targetPlayer.getName()));
    }

    @Test
    void banPermissionDenied() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock player = server.addPlayer();
        player.setOp(false);

        player.performCommand("syn ban someone");

        String output = player.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("permission"));
    }
}
