package com.rock_mc.syn.db;

import com.google.common.collect.Lists;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLite extends Database {
    private final JavaPlugin plugin;

    private static final LoggerPlugin LOG_PLUGIN = new LoggerPlugin();

    public SQLite(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "database.db");
    }

    @Override
    public void load() {
        // 插件啟動時建立數據庫連接
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = getConnection();
            createTable(connection);

            LOG_PLUGIN.logInfo("Connected to SQLite");
        } catch (ClassNotFoundException | SQLException e) {
            LOG_PLUGIN.logSevere("Could not connect to SQLite database: " + e.getMessage());
            throw new IllegalStateException("Could not connect to SQLite database", e);
        }
    }

    private void createTable(Connection connection) {
        // Table: verification_codes
        // Create table if not exists
        // Verification code, created time, used
        // the verification code is unique
        // created time is the time when the code is created
        // used is a boolean indicating whether the code has been used
        // the verification code, created time, and used are indexed
        try (Statement statement = connection.createStatement();) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS verification_codes (
					    code TEXT PRIMARY KEY,
					    player_used TEXT DEFAULT NULL,
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
					""");

            // Create indexes on code, created_at, and used columns
            statement.execute("CREATE INDEX IF NOT EXISTS idx_verification_codes_code ON verification_codes (code)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create verification_codes table or indexes: " + e.getMessage());
        }

        // Table: player_info
        // Create table if not exists
        // Player uuid, player name, created time
        // the player uuid is unique
        // the player name is a string
        // created time is the time when the player is added
        // the player uuid is indexed
        try (Statement statement = connection.createStatement()) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS player_info (
					    player_uuid TEXT PRIMARY KEY,
					    player_name TEXT NOT NULL,
					    last_login TIMESTAMP,
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
					""");
            // Create index on player_uuid column
            statement.execute("CREATE INDEX IF NOT EXISTS idx_player_info_player_uuid ON player_info (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create player_info table or index: " + e.getMessage());
        }

        // Table: allowed_players
        // Create table if not exists
        // Player uuid, created time
        // the player uuid is unique
        // created time is the time when the player is added
        // the player uuid is indexed
        try (Statement statement = connection.createStatement()) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS allowed_players (
					    player_uuid TEXT PRIMARY KEY,
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
					""");

            // Create index on player_uuid column
            statement.execute("CREATE INDEX IF NOT EXISTS idx_allowed_players_player_uuid ON allowed_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create allowed_players table or index: " + e.getMessage());
        }

        // Table: baned_players
        // Create table if not exists
        // Player uuid, reason, expire_time, created time
        // the player uuid is unique
        // the reason is a string
        // expire time is the time the player is banned until
        // created time is the time when the player is added
        // the player uuid is indexed
        try (Statement statement = connection.createStatement()) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS baned_players (
					    player_uuid TEXT PRIMARY KEY,
					    reason TEXT NOT NULL,
					    expire_time INTEGER DEFAULT 0,
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
					""");

            // Create index on player_uuid column
            statement.execute("CREATE INDEX IF NOT EXISTS idx_baned_players_player_uuid ON baned_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create baned_players table or index: " + e.getMessage());
        }

        // Table: failed_players
        // Create table if not exists
        // Player uuid, fail time, created time
        // the player uuid is unique
        // fail time is the time the player failed to verify

        try (Statement statement = connection.createStatement()) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS failed_players (
					    player_uuid TEXT PRIMARY KEY,
					    fail_time INTEGER DEFAULT 0,
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
					""");

            // Create index on player_uuid column
            statement.execute("CREATE INDEX IF NOT EXISTS idx_failed_players_player_uuid ON failed_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create failed_players table or index: " + e.getMessage());
        }

        // Table: event_logs
        // Create table if not exists
        // id, Player uuid, event name, created time
        // the event name: login, logout, ban, unban, etc.
        // NULL in player uuid is server event

        try (Statement statement = connection.createStatement()) {
			statement.execute("""
					CREATE TABLE IF NOT EXISTS event_logs (
					    id integer primary key autoincrement
					    player_uuid TEXT DEFAULT NULL
					    event_name TEXT DEFAULT NULL
					    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
					""");

        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create log table: " + e.getMessage());
        }
    }

    @Override
    public void addCode(String code) {
        // Add code to the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO verification_codes (code) VALUES (?)");
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add code to the database: " + e.getMessage());
        }
    }

    @Override
    public void addBanedPlayer(String playerUUID, String reason, long time) {
        // Add baned player to the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO baned_players (player_uuid, reason, expire_time) VALUES (?, ?, ?)");

            statement.setString(1, playerUUID);
            statement.setString(2, reason);
            statement.setLong(3, time);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add baned player to the database: " + e.getMessage());
        }
    }

    @Override
    public String getCodeCreateDate(String code) {
        // Get the created time of the code
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT created_at FROM verification_codes WHERE code = ?");

            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("created_at");
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the created time of the code: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean contains(String code) {
        // Check if the code exists in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT code FROM verification_codes WHERE code = ? and player_used = null");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the code exists in the database: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void markCode(String code, String playerUUID) {
        // Mark the code as used or not used
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO verification_codes (code, player_used) VALUES (?, ?) ON CONFLICT(code) DO UPDATE SET player_used = ?");
            statement.setString(1, code);
            statement.setString(2, playerUUID);
            statement.setString(3, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not mark the code as used or not used: " + e.getMessage());
        }
    }

    @Override
    public void addAllowedPlayer(String playerUUID) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO allowed_players (player_uuid) VALUES (?) ON CONFLICT(player_uuid) DO NOTHING");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add player to the database: " + e.getMessage());
        }
    }

    @Override
    public void removeAllowedPlayer(String playerUUID) {
        // Remove player from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM allowed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove player from the database: " + e.getMessage());
        }
    }

    @Override
    public boolean isPlayerAllowed(String playerUUID) {
        // Check if the player is allowed
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT player_uuid FROM allowed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the player is allowed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isPlayerBanned(String playerUUID) {
        // Check if the player is banned
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT player_uuid FROM baned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the player is banned: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        // Update the number of failed attempts of the player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO failed_players (player_uuid, fail_time) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET fail_time = ?");
            statement.setString(1, playerUUID);
            statement.setInt(2, failedAttempts);
            statement.setInt(3, failedAttempts);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not update the fail time of the player: " + e.getMessage());
        }
    }

    @Override
    public void removeCode(String code) {
        // Remove code from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM verification_codes WHERE code = ?");
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove code from the database: " + e.getMessage());
        }
    }

    @Override
    public int getFailedAttempts(String playerUUID) {
        // Get the number of failed attempts of the player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT fail_time FROM failed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("fail_time");
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the fail time of the player: " + e.getMessage());
        }
        return 1;
    }

    @Override
    public long getBannedExpireTime(String playerUUID) {
        // Get the expiry time of the player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT expire_time FROM baned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("expire_time");
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the expire time of the player: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void removeBanedPlayer(String playerUUID) {
        // Remove baned player from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM baned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove baned player from the database: " + e.getMessage());
        }
    }

    @Override
    public void removeFailedPlayer(String playerUUID) {
        // Remove baned player from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM failed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove failed player from the database: " + e.getMessage());
        }
    }

    @Override
    public String getBannedReason(String playerUUID) {
        // Get the reason of the player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT reason FROM baned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("reason");
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the reason of the player: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getBannedCreateAt(String playerUUID) {
        // Get the created time of the player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT created_at FROM baned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("created_at");
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the created time of the player: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addPlayerInfo(String playerUUID, String playerName) {
        // Add or update player info in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO player_info (player_uuid, player_name, last_login) VALUES (?, ?, CURRENT_TIMESTAMP) ON CONFLICT(player_uuid) DO UPDATE SET player_name = ?, last_login = CURRENT_TIMESTAMP");
            statement.setString(1, playerUUID);
            statement.setString(2, playerName);
            statement.setString(3, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add or update player info in the database: " + e.getMessage());
        }
    }

    @Override
    public boolean isCodeUsed(String code) {
        // Check if the code is used
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT player_used FROM verification_codes WHERE code = ?");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("player_used") != null;
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the code is used: " + e.getMessage());
        }
        return false;
    }


    @Override
    public PluginPlayerInfo getPlayerByName(String playerName) {
        // Check if the code is used
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT player_uuid, 
                           player_name, 
                           last_login, 
                           created_at
                    FROM player_info WHERE player_name = ?
                    """);
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new PluginPlayerInfo(
                        resultSet.getString("player_uuid"),
                        resultSet.getString("player_name"),
                        resultSet.getTimestamp("last_login"),
                        resultSet.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not find the player: " + playerName + " " + e.getMessage());
        }
        return null;
    }

    @Override
    public String[] getBannedPlayerList() {
        // Get the name list of banned players
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT player_name FROM player_info WHERE player_uuid IN (SELECT player_uuid FROM baned_players)"
             )) {
            ResultSet resultSet = statement.executeQuery();
            List<String> bannedPlayers = new ArrayList<>();
            while (resultSet.next()) {
                bannedPlayers.add(resultSet.getString("player_name"));
            }
            return bannedPlayers.toArray(new String[0]);
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the list of banned players: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addLogEvent(String playerUUID, String eventName) {
        // Add baned player to the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO event_logs (player_uuid, event_name) VALUES (?, ?)");

            statement.setString(1, playerUUID);
            statement.setString(2, eventName);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add event_logs to the database: " + e.getMessage());
        }
    }

    @Override
    public List<EventLog> getLogEvents(String playerUUID, Timestamp start, Timestamp end) {
        List<EventLog> eventLogs = Lists.newArrayList();
        if (start == null) {
            // default 3 months ago
            start = new Timestamp(System.currentTimeMillis() - 3L * 30 * 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Timestamp(System.currentTimeMillis());
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM event_logs WHERE (player_uuid = ? OR player_uuid IS NULL) AND created_at BETWEEN ? AND ?"
             )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                EventLog eventLog = new EventLog(
                        resultSet.getLong("id"),
                        resultSet.getString("player_uuid"),
                        resultSet.getString("event_name"),
                        resultSet.getTimestamp("created_at")
                );
                eventLogs.add(eventLog);
            }
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the list of banned players: " + e.getMessage());
        }
        return eventLogs;
    }

    @Override
    public void save() {
        // Save data to the database
        // No need to implement this method for SQLite
    }

    @Override
    public void close() {
        // Close the database connection
        // No need to implement this method for SQLite
    }
}
