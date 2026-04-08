package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuestInfoReloadTest extends PluginTest {

    @Test
    void guestToggle() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        // Guest mode starts as false
        assertFalse(plugin.configManager.getConfig().getBoolean(Config.GUEST));

        // Toggle on
        opPlayer.performCommand("syn guest");
        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("訪客模式已經設定為"));
        assertTrue(plugin.configManager.getConfig().getBoolean(Config.GUEST));
        // Consume second message
        opPlayer.nextMessage();

        // Toggle off
        opPlayer.performCommand("syn guest");
        output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("訪客模式已經設定為"));
        assertFalse(plugin.configManager.getConfig().getBoolean(Config.GUEST));
    }

    @Test
    void guestPermissionDenied() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock player = server.addPlayer();
        player.setOp(false);

        player.performCommand("syn guest");

        String output = player.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("permission"));
    }

    @Test
    void infoCommand() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        // Create a player with info
        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());

        opPlayer.performCommand("syn info " + targetPlayer.getName());

        // Should show player info
        String output = opPlayer.nextMessage();
        assertNotNull(output);

        // Collect all messages
        StringBuilder allOutput = new StringBuilder(output);
        String msg;
        while ((msg = opPlayer.nextMessage()) != null) {
            allOutput.append("\n").append(msg);
        }

        String fullOutput = allOutput.toString();
        assertTrue(fullOutput.contains("使用者"));
        assertTrue(fullOutput.contains(targetPlayer.getName()));
    }

    @Test
    void infoNonExistentPlayer() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        opPlayer.performCommand("syn info nonexistent");

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("查無此玩家"));
    }

    @Test
    void reloadCommand() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        plugin.dbManager.addPlayerToAllowList(opPlayer.getUniqueId().toString());

        // Add some data to cache
        PlayerMock targetPlayer = server.addPlayer();
        plugin.dbManager.addPlayerInfo(targetPlayer.getUniqueId().toString(), targetPlayer.getName());
        plugin.dbManager.isPlayerInAllowList(targetPlayer.getUniqueId().toString());

        // Reload should clear caches and reload config
        opPlayer.performCommand("syn reload");

        String output = opPlayer.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("reload"));
    }

    @Test
    void reloadPermissionDenied() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock player = server.addPlayer();
        player.setOp(false);

        player.performCommand("syn reload");

        String output = player.nextMessage();
        assertNotNull(output);
        assertTrue(output.contains("permission"));
    }
}
