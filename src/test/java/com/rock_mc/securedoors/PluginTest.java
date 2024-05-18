package com.rock_mc.securedoors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PluginTest {

    protected ServerMock server;

    protected SecureDoors plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        plugin = MockBukkit.load(SecureDoors.class);
    }

    @AfterEach
    void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }
}
