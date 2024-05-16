package com.rock_mc.securedoors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class PlayerCommandTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        SecureDoors plugin = MockBukkit.load(SecureDoors.class);
    }

    @AfterEach
    void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    void onCommandForOp() {
        // 管理員
        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        opPlayer.performCommand("sd");
        assertEquals(Log.LOG_PREFIX + "verify | gencode | block | unblock | give | list", opPlayer.nextMessage());

    }

    @Test
    void onCommandForVerified() {
        // 已認證使用者
        PlayerMock playerVerified = server.addPlayer();

        playerVerified.performCommand("sd");
        assertEquals(Log.LOG_PREFIX + "gencode", playerVerified.nextMessage());

    }

    @Test
    void onCommandForNew() {
        // 新玩家
        PlayerMock newPlayer = server.addPlayer();
        newPlayer.setName("guest");

        newPlayer.performCommand("sd");
        assertEquals(Log.LOG_PREFIX + "verify <invttation code>", newPlayer.nextMessage());
    }

}