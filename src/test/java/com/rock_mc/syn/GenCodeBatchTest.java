package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GenCodeBatchTest extends PluginTest {

    @Test
    void batchGenCode() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        // Generate 50 codes at once (batch insert)
        opPlayer.performCommand("syn gencode 50");

        String output = opPlayer.nextMessage();
        assertNotNull(output);

        // Extract codes from output
        String msgUrl = "https://rock-mc.com/code/?text=";
        String[] lines = output.split("\n");

        Set<String> codes = new HashSet<>();
        for (String line : lines) {
            line = line.trim();
            if (line.contains(msgUrl)) {
                String code = line.substring(line.lastIndexOf(msgUrl) + msgUrl.length());
                if (!code.isEmpty()) {
                    codes.add(code);
                }
            }
        }

        // All 50 codes should be unique
        assertEquals(50, codes.size());

        // All codes should exist in the database
        for (String code : codes) {
            assertFalse(plugin.dbManager.isCodeUsed(code), "Code should not be used yet: " + code);
        }
    }

    @Test
    void batchGenCodeVerifyOne() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        opPlayer.performCommand("syn gencode 5");

        String output = opPlayer.nextMessage();
        assertNotNull(output);

        // Extract the last code
        String msgUrl = "https://rock-mc.com/code/?text=";
        String code = output.substring(output.lastIndexOf(msgUrl) + msgUrl.length());

        // Use one code to verify a new player
        PlayerMock newPlayer = server.addPlayer();
        newPlayer.performCommand("syn verify " + code);

        String verifyOutput = newPlayer.nextMessage();
        assertNotNull(verifyOutput);
        assertTrue(verifyOutput.contains("歡迎"));

        // That code should now be used
        assertTrue(plugin.dbManager.isCodeUsed(code));
    }
}
