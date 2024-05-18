package com.rock_mc.securedoors.config;

import com.rock_mc.securedoors.PluginTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest extends PluginTest {

    @Test
    void checkNotExistValue() {
        ConfigManager configManager = plugin.getConfigManager();

        Object value = configManager.getConfig().get("door.notExist");

        assertNull(value);
    }

    @Test
    void checkOpen() {
        ConfigManager configManager = plugin.getConfigManager();

        assertFalse(configManager.getConfig().getBoolean("door.open"));

        configManager.getConfig().set("door.open", true);

        assertTrue(configManager.getConfig().getBoolean("door.open"));
    }
}