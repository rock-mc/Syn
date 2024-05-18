package com.rock_mc.securedoors.db;

import com.rock_mc.securedoors.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DbManager {

    private final JavaPlugin plugin;
    private ConfigManager configManager;
    private Database database;

    public DbManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

        if ("sqlite".equalsIgnoreCase(configManager.getConfig().getString("door.database.type"))) {
            this.database = new SQLite(this.plugin);
        }
    }

    public void load() {
        this.database.load();
    }

    public void save() {
        this.database.save();
    }

    public void close() {
        this.database.close();
    }

    public void addCode(String code) {
        this.database.addCode(code);
    }
    public String getCodeCreateDate(String code) {
        return this.database.getCodeCreateDate(code);
    }
    public boolean contains(String code) {
        return this.database.contains(code);
    }

    public void removeCode(String code) {
        this.database.removeCode(code);
    }
}
