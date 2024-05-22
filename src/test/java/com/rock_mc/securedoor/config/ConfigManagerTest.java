<<<<<<<< HEAD:src/test/java/com/rock_mc/syn/config/ConfigManagerTest.java
package com.rock_mc.syn.config;

import com.rock_mc.syn.PluginTest;
========
package com.rock_mc.securedoor.config;

import com.rock_mc.securedoor.PluginTest;
>>>>>>>> main:src/test/java/com/rock_mc/securedoor/config/ConfigManagerTest.java
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