package com.rock_mc.securedoors.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.securedoors.utils.Log;
import com.rock_mc.securedoors.PluginTest;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SdCommandTest extends PluginTest {



    @Test
    void onCommandByOp() {
        // 管理員
        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        String expected = Log.LOG_PREFIX + "verify | gencode | block | unblock | give | list";

        opPlayer.performCommand("sd");
        assertEquals(expected, opPlayer.nextMessage());

        opPlayer.performCommand("sd help");
        assertEquals(expected, opPlayer.nextMessage());
    }

    @Test
    void onCommandByVerified() {
        // 已認證使用者
        PlayerMock playerVerified = server.addPlayer();

        String expected = Log.LOG_PREFIX + "gencode";

        playerVerified.performCommand("sd");
        assertEquals(expected, playerVerified.nextMessage());

        playerVerified.performCommand("sd help");
        assertEquals(expected, playerVerified.nextMessage());

    }

    @Test
    void onCommandByNew() {
        // 新玩家
        PlayerMock newPlayer = server.addPlayer();
        newPlayer.setName("guest");

        String expected = Log.LOG_PREFIX + "verify <invitation code>";

        newPlayer.performCommand("sd");
        assertEquals(expected, newPlayer.nextMessage());

        newPlayer.performCommand("sd help");
        assertEquals(expected, newPlayer.nextMessage());
    }

}