package com.rock_mc.invitsplugin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.plugin.messaging.Messenger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerCommandTest {

    private ServerMock server;
    private InvitsPlugin plugin;

    @BeforeEach
    void setUp() {
        // Start the mock server
        server = MockBukkit.mock();
        // Load your plugin
        plugin = MockBukkit.load(InvitsPlugin.class);
    }

    @AfterEach
    void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    void onCommand() {
        PlayerMock player = server.addPlayer();
        player.performCommand("invits");

        assertTrue(player.nextMessage().contains("invits"));

    }
}