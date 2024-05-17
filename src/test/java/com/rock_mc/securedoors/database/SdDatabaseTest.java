package com.rock_mc.securedoors.database;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.securedoors.SecureDoors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SdDatabaseTest {

    private ServerMock server;
    private SecureDoors plugin;

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

    @Test
    void testWhiteList_InvitationQuota() throws SQLException {

        SdDatabase db = plugin.getSdDatabase();

        PlayerMock player = server.addPlayer();

        // 玩家加入白名單
        db.addPlayerToWhiteList(player);

        // 檢查是否存在於白名單
        assertTrue(db.isPlayerInWhiteList(player));

        // 設定玩家邀請數量
        db.updatePlayerInvitationQuota(player, 1);

        // 查詢玩家邀請數量
        assertEquals(1, db.findPlayerInvitationQuota(player));
    }
}