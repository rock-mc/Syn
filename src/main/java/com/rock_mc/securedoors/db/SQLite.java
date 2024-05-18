package com.rock_mc.securedoors.db;

import com.rock_mc.securedoors.Log;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {
    private Connection connection;
    private final JavaPlugin plugin;

    public SQLite(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        // 插件啟動時建立數據庫連接
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "database.db");
            createTable();

            Log.logInfo("Connected to SQLite database.");
        } catch (ClassNotFoundException | SQLException e) {
            Log.logWarning("Could not connect to SQLite database: " + e.getMessage());
        }
    }

    private void createTable() {
        // Table: verification_codes
        // Create table if not exists
        // Verification code, created time
        // the verification code is unique
        // created time is the time when the code is created
        // the verification code is indexed
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS verification_codes (" +
                    "code TEXT PRIMARY KEY," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create index on code column
            statement.execute("CREATE INDEX IF NOT EXISTS idx_verification_codes_code ON verification_codes (code)");

            Log.logInfo("Created verification_codes table and index.");
        } catch (SQLException e) {
            Log.logWarning("Could not create verification_codes table or index: " + e.getMessage());
        }

        // Table: allowed_players
        // Create table if not exists
        // Player uuid, player name, created time
        // the player uuid is unique
        // the player name is a string
        // created time is the time when the player is added
        // the player uuid is indexed

    }

    @Override
    public void addCode(String code) {
        // Add code to the database
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO verification_codes (code) VALUES (?)")) {
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            Log.logWarning("Could not add code to the database: " + e.getMessage());
        }
    }

    @Override
    public String getCodeCreateDate(String code) {
        // Get the created time of the code
        try (PreparedStatement statement = connection.prepareStatement("SELECT created_at FROM verification_codes WHERE code = ?")) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("created_at");
            }
        } catch (SQLException e) {
            Log.logWarning("Could not get the created time of the code: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void removeCode(String code) {
        // Remove code from the database
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM verification_codes WHERE code = ?")) {
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            Log.logWarning("Could not remove code from the database: " + e.getMessage());
        }
    }

    @Override
    public void save() {
        // Save data to the database
        // No need to implement this method for SQLite
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Log.logInfo("Closed SQLite database connection.");
            }
        } catch (SQLException e) {
            Log.logWarning("Could not close SQLite database connection: " + e.getMessage());
        }
    }

    @Override
    public boolean contains(String code) {
        // Get the created time of the code
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT created_at FROM verification_codes WHERE code = ?
            AND julianday('now') - julianday(created_at) <= ?;
            """)) {
            statement.setString(1, code);

            int expireDay = this.plugin.getConfig().getInt("door.expire_day");
            statement.setInt(2, expireDay);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.logWarning("Could not get a valid code: " + e.getMessage());
        }
        return false;
    }
}
