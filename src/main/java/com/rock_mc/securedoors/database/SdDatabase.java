package com.rock_mc.securedoors.database;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SdDatabase {

    private final Connection connection;

    public SdDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS whitelist (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "invitationQuota INTEGER NOT NULL DEFAULT 0)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayerToWhiteList(Player player) throws SQLException {
        //this should error if the player already exists
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO whitelist (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean isPlayerInWhiteList(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelist WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void updatePlayerInvitationQuota(Player player, int invitationQuota) throws SQLException{

        //if the player doesn't exist, add them
        if (!isPlayerInWhiteList(player)){
            addPlayerToWhiteList(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE whitelist SET invitationQuota = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, invitationQuota);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int findPlayerInvitationQuota(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT invitationQuota FROM whitelist WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("invitationQuota");
            } else {
                return 0; // Return 0 if the player has no invitationQuota
            }
        }
    }

}
