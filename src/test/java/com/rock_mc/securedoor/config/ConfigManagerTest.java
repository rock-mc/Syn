package com.rock_mc.securedoor.config;

import com.rock_mc.securedoor.PluginTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest extends PluginTest {

    @Test
    void checkNotExistValue() {

        Object value = plugin.configManager.getConfig().get("door.notExist");

        assertNull(value);
    }

    @Test
    void checkOpen() {
        ConfigManager configManager = plugin.configManager;

        assertFalse(configManager.getConfig().getBoolean("door.open"));

        configManager.getConfig().set("door.open", true);

        assertTrue(configManager.getConfig().getBoolean("door.open"));
    }
}