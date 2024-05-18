package com.rock_mc.securedoors.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.securedoors.utils.Log;
import com.rock_mc.securedoors.PluginTest;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class CommandTest extends PluginTest {



    @Test
    void onCommandByOp() {
        // 管理員
        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        String expected = Log.PREFIX_GAME + """
Commands:
* gencode: Generate a verification code
Usage: /sd gencode
* info: Show the door information
Usage: /sd info
* block: Block the door
Usage: /sd block
* unblock: Unblock the door
Usage: /sd unblock""";

        opPlayer.performCommand("sd");

        String commandOutput = opPlayer.nextMessage();
        assertEquals(expected, commandOutput);

        opPlayer.performCommand("sd help");
        assertEquals(expected, opPlayer.nextMessage());
    }
}