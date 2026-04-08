package com.rock_mc.syn.db;

import com.google.common.collect.Lists;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLite extends Database {
    private final JavaPlugin plugin;
    private Connection persistentConnection;

    private static final LoggerPlugin LOG_PLUGIN = new LoggerPlugin();

    public SQLite(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (persistentConnection == null || persistentConnection.isClosed()) {
            persistentConnection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "database.db");
            try (Statement stmt = persistentConnection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
            }
        }
        return persistentConnection;
    }

    @Override
    public void load() {
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
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS verification_codes (
                        code TEXT PRIMARY KEY,
                        player_used TEXT DEFAULT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_verification_codes_code ON verification_codes (code)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create verification_codes table or indexes: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS player_info (
                        player_uuid TEXT PRIMARY KEY,
                        player_name TEXT NOT NULL,
                        last_login TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_player_info_player_uuid ON player_info (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create player_info table or index: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS allowed_players (
                        player_uuid TEXT PRIMARY KEY,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_allowed_players_player_uuid ON allowed_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create allowed_players table or index: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS banned_players (
                        player_uuid TEXT PRIMARY KEY,
                        reason TEXT NOT NULL,
                        expire_time INTEGER DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_banned_players_player_uuid ON banned_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create banned_players table or index: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS failed_players (
                        player_uuid TEXT PRIMARY KEY,
                        fail_time INTEGER DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_failed_players_player_uuid ON failed_players (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create failed_players table or index: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS event_logs (
                        id integer primary key autoincrement,
                        player_uuid TEXT DEFAULT NULL,
                        event_name TEXT DEFAULT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )
                    """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_event_logs_created_at ON event_logs (created_at)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_event_logs_player_uuid ON event_logs (player_uuid)");
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not create event_logs table: " + e.getMessage());
        }
    }

    @Override
    public void addCode(String code) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO verification_codes (code) VALUES (?)");
            statement.setString(1, code);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add code to the database: " + e.getMessage());
        }
    }

    @Override
    public void addCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) return;
        try {
            Connection connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO verification_codes (code) VALUES (?)");
            for (String code : codes) {
                statement.setString(1, code);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not batch add codes to the database: " + e.getMessage());
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void addBannedPlayer(String playerUUID, String reason, long time) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO banned_players (player_uuid, reason, expire_time) VALUES (?, ?, ?)");
            statement.setString(1, playerUUID);
            statement.setString(2, reason);
            statement.setLong(3, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add banned player to the database: " + e.getMessage());
        }
    }

    @Override
    public String getCodeCreateDate(String code) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT created_at FROM verification_codes WHERE code = ?");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            String result = null;
            if (resultSet.next()) {
                result = resultSet.getString("created_at");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the created time of the code: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean contains(String code) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT code FROM verification_codes WHERE code = ? AND player_used IS NULL");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the code exists in the database: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void markCode(String code, String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO verification_codes (code, player_used) VALUES (?, ?) ON CONFLICT(code) DO UPDATE SET player_used = ?");
            statement.setString(1, code);
            statement.setString(2, playerUUID);
            statement.setString(3, playerUUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not mark the code as used or not used: " + e.getMessage());
        }
    }

    @Override
    public void addAllowedPlayer(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO allowed_players (player_uuid) VALUES (?) ON CONFLICT(player_uuid) DO NOTHING");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add player to the database: " + e.getMessage());
        }
    }

    @Override
    public void removeAllowedPlayer(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM allowed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove player from the database: " + e.getMessage());
        }
    }

    @Override
    public boolean isPlayerAllowed(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT player_uuid FROM allowed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the player is allowed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isPlayerBanned(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT player_uuid FROM banned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the player is banned: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void updateFailedAttempts(String playerUUID, int failedAttempts) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO failed_players (player_uuid, fail_time) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET fail_time = ?");
            statement.setString(1, playerUUID);
            statement.setInt(2, failedAttempts);
            statement.setInt(3, failedAttempts);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not update the fail time of the player: " + e.getMessage());
        }
    }

    @Override
    public void removeCode(String code) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM verification_codes WHERE code = ?");
            statement.setString(1, code);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove code from the database: " + e.getMessage());
        }
    }

    @Override
    public int getFailedAttempts(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT fail_time FROM failed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            int result = 0;
            if (resultSet.next()) {
                result = resultSet.getInt("fail_time");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the fail time of the player: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public long getBannedExpireTime(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT expire_time FROM banned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            long result = -1;
            if (resultSet.next()) {
                result = resultSet.getLong("expire_time");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the expire time of the player: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void removeBannedPlayer(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM banned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove banned player from the database: " + e.getMessage());
        }
    }

    @Override
    public void removeFailedPlayer(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM failed_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not remove failed player from the database: " + e.getMessage());
        }
    }

    @Override
    public String getBannedReason(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT reason FROM banned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            String result = null;
            if (resultSet.next()) {
                result = resultSet.getString("reason");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the reason of the player: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getBannedCreateAt(String playerUUID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT created_at FROM banned_players WHERE player_uuid = ?");
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            String result = null;
            if (resultSet.next()) {
                result = resultSet.getString("created_at");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the created time of the player: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addPlayerInfo(String playerUUID, String playerName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO player_info (player_uuid, player_name, last_login) VALUES (?, ?, CURRENT_TIMESTAMP) ON CONFLICT(player_uuid) DO UPDATE SET player_name = ?, last_login = CURRENT_TIMESTAMP");
            statement.setString(1, playerUUID);
            statement.setString(2, playerName);
            statement.setString(3, playerName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add or update player info in the database: " + e.getMessage());
        }
    }

    @Override
    public boolean isCodeUsed(String code) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT player_used FROM verification_codes WHERE code = ?");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            boolean result = false;
            if (resultSet.next()) {
                result = resultSet.getString("player_used") != null;
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not check if the code is used: " + e.getMessage());
        }
        return false;
    }

    @Override
    public PluginPlayerInfo getPlayerByName(String playerName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("""
                    SELECT player_uuid,
                           player_name,
                           last_login,
                           created_at
                    FROM player_info WHERE lower(player_name) = lower(?)
                    """);
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            PluginPlayerInfo result = null;
            if (resultSet.next()) {
                result = new PluginPlayerInfo(resultSet.getString("player_uuid"), resultSet.getString("player_name"), resultSet.getTimestamp("last_login"), resultSet.getTimestamp("created_at"));
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not find the player: " + playerName + " " + e.getMessage());
        }
        return null;
    }

    @Override
    public String[] getBannedPlayerList() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT player_name FROM player_info WHERE player_uuid IN (SELECT player_uuid FROM banned_players)");
            ResultSet resultSet = statement.executeQuery();
            List<String> bannedPlayers = new ArrayList<>();
            while (resultSet.next()) {
                bannedPlayers.add(resultSet.getString("player_name"));
            }
            resultSet.close();
            statement.close();
            return bannedPlayers.toArray(new String[0]);
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the list of banned players: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addLogEvent(String playerUUID, String eventName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO event_logs (player_uuid, event_name) VALUES (?, ?)");
            statement.setString(1, playerUUID);
            statement.setString(2, eventName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not add event_logs to the database: " + e.getMessage());
        }
    }

    @Override
    public List<EventLog> getLogEvents(List<String> playerUUIDs, Timestamp start, Timestamp end, Integer page, Integer rows) {
        List<EventLog> eventLogs = Lists.newArrayList();
        playerUUIDs = playerUUIDs == null ? Collections.emptyList() : playerUUIDs;
        Timestamp startTimestamp = start == null ? new Timestamp(System.currentTimeMillis() - 3L * 30 * 24 * 60 * 60 * 1000) : start;
        Timestamp endTimestamp = end == null ? new Timestamp(System.currentTimeMillis()) : end;
        StringBuilder sqlbuilder = new StringBuilder("SELECT e.*, p.player_name FROM event_logs e LEFT JOIN player_info p ON p.player_uuid = e.player_uuid WHERE 1=1 ");
        if (!playerUUIDs.isEmpty()) {
            String placeholders = String.join(",", Collections.nCopies(playerUUIDs.size(), "?"));
            sqlbuilder.append("AND e.player_uuid IN (").append(placeholders).append(") ");
        }
        sqlbuilder.append("AND e.created_at BETWEEN ? AND ? ");
        sqlbuilder.append("LIMIT ? OFFSET ? ");
        try {
            PreparedStatement statement = getConnection().prepareStatement(sqlbuilder.toString());
            int i = 0;
            for (String uuid : playerUUIDs) {
                statement.setString(++i, uuid);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            statement.setString(++i, sdf.format(startTimestamp));
            statement.setString(++i, sdf.format(endTimestamp));
            statement.setInt(++i, rows);
            statement.setInt(++i, (page-1)*rows);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                EventLog eventLog = new EventLog(
                        resultSet.getLong("id"),
                        resultSet.getString("player_uuid"),
                        resultSet.getString("event_name"),
                        resultSet.getTimestamp("created_at"),
                        resultSet.getString("player_name"));
                eventLogs.add(eventLog);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the list of event logs: " + e.getMessage());
        }
        return eventLogs;
    }

    @Override
    public Long countLogEvent(List<String> playerUUIDs, Timestamp start, Timestamp end) {
        playerUUIDs = playerUUIDs == null ? Collections.emptyList() : playerUUIDs;
        Timestamp startTimestamp = start == null ? new Timestamp(System.currentTimeMillis() - 3L * 30 * 24 * 60 * 60 * 1000) : start;
        Timestamp endTimestamp = end == null ? new Timestamp(System.currentTimeMillis()) : end;
        StringBuilder sqlbuilder = new StringBuilder("SELECT COUNT(0) AS count FROM event_logs e WHERE 1=1 ");
        if (!playerUUIDs.isEmpty()) {
            String placeholders = String.join(",", Collections.nCopies(playerUUIDs.size(), "?"));
            sqlbuilder.append("AND e.player_uuid IN (").append(placeholders).append(") ");
        }
        sqlbuilder.append("AND e.created_at BETWEEN ? AND ? ");
        try {
            PreparedStatement statement = getConnection().prepareStatement(sqlbuilder.toString());
            int i = 0;
            for (String uuid : playerUUIDs) {
                statement.setString(++i, uuid);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            statement.setString(++i, sdf.format(startTimestamp));
            statement.setString(++i, sdf.format(endTimestamp));

            ResultSet resultSet = statement.executeQuery();
            long result = 0L;
            if (resultSet.next()) {
                result = resultSet.getLong("count");
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            LOG_PLUGIN.logWarning("Could not get the count of event logs: " + e.getMessage());
        }
        return 0L;
    }

    @Override
    public void save() {
        // No need to implement this method for SQLite
    }

    @Override
    public synchronized void close() {
        if (persistentConnection != null) {
            try {
                persistentConnection.close();
            } catch (SQLException e) {
                LOG_PLUGIN.logWarning("Could not close SQLite connection: " + e.getMessage());
            }
            persistentConnection = null;
        }
    }
}
