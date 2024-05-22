<<<<<<<< HEAD:src/test/java/com/rock_mc/syn/PluginTest.java
package com.rock_mc.syn;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.rock_mc.syn.db.DbManager;
========
package com.rock_mc.securedoor;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.rock_mc.securedoor.db.DbManager;
>>>>>>>> main:src/test/java/com/rock_mc/securedoor/PluginTest.java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PluginTest {

    protected ServerMock server;

<<<<<<<< HEAD:src/test/java/com/rock_mc/syn/PluginTest.java
    protected Syn plugin;
========
    protected SecureDoor plugin;
>>>>>>>> main:src/test/java/com/rock_mc/securedoor/PluginTest.java

    protected DbManager dbManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

<<<<<<<< HEAD:src/test/java/com/rock_mc/syn/PluginTest.java
        plugin = MockBukkit.load(Syn.class);
========
        plugin = MockBukkit.load(SecureDoor.class);
>>>>>>>> main:src/test/java/com/rock_mc/securedoor/PluginTest.java

        dbManager = new DbManager(plugin);
    }

    @AfterEach
    void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }
}
