package com.rock_mc.securedoors.utils;

import com.rock_mc.securedoors.PluginTest;
import com.rock_mc.securedoors.config.ConfigManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest extends PluginTest {

    @Test
    void isOpen() {
        ConfigManager configManager = plugin.getConfigManager();

        assertTrue(configManager.getConfig().getBoolean("open"));

        configManager.getConfig().set("open", false);

        assertFalse(configManager.getConfig().getBoolean("open"));
    }
}