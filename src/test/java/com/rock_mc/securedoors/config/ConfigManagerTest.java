package com.rock_mc.securedoors.config;

import com.rock_mc.securedoors.PluginTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest extends PluginTest {

    @Test
    void checkOpen() {
        ConfigManager configManager = plugin.getConfigManager();

        assertFalse(configManager.getConfig().getBoolean("open"));

        configManager.getConfig().set("open", true);

        assertTrue(configManager.getConfig().getBoolean("open"));
    }
}