package com.rock_mc.securedoors.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.securedoors.Log;
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
* ban: Ban the player
Usage: /sd ban <player>
* unban: Unban the door
Usage: /sd unban <player>
* open: Allow everyone to come into the server but the player in the ban list
Usage: /sd open
* close: Allow the player in the allowlist to come into the server
Usage: /sd close""";

        opPlayer.performCommand("sd");

        String commandOutput = opPlayer.nextMessage();
        System.out.println(commandOutput);
        assertEquals(expected, commandOutput);

        opPlayer.performCommand("sd help");
        assertEquals(expected, opPlayer.nextMessage());
    }
}