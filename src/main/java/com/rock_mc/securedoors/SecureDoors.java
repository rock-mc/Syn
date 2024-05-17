package com.rock_mc.securedoors;

import com.rock_mc.securedoors.database.SdDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "SecureDoors";
    private static final String SQLite_FILE = "secureDoors.db";

    private SdDatabase db;

    @Override
    public void onEnable() {
        Log.logger = getLogger();

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            String dbFile = getDataFolder().getAbsolutePath() + File.separator + SQLite_FILE;
            db = new SdDatabase(dbFile);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        this.getCommand("sd").setExecutor(new PlayerCommand());

        Log.server("Enable", "Complete");

    }

    @Override
    public void onDisable() {
        try {
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SdDatabase getSdDatabase() {
        return this.db;
    }


}
