package com.rock_mc.securedoors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.rock_mc.securedoors.db.DbManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PluginTest {

    protected ServerMock server;

    protected SecureDoors plugin;

    protected DbManager dbManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        plugin = MockBukkit.load(SecureDoors.class);

        dbManager = new DbManager(plugin);
    }

    @AfterEach
    void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }
}
