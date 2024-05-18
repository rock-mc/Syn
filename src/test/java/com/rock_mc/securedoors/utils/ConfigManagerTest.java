package com.rock_mc.securedoors.utils;

import com.rock_mc.securedoors.PluginTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest extends PluginTest {

    @Test
    void isOpen() {
        ConfigManager configManager = plugin.getConfigManager();

        assertTrue(configManager.isOpen());

        configManager.setOpen(false);

        assertFalse(configManager.isOpen());
    }
}