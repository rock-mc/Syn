package com.rock_mc.securedoors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final JavaPlugin plugin ;
    private FileConfiguration config = null;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        // Check if the configuration is valid
        boolean valid = true;
        if (config.get("door.open") == null) {
            valid = false;
        }
        if (config.get("door.available_characters") == null) {
            valid = false;
        }
        if (config.get("door.code_length") == null) {
            valid = false;
        }
        if (config.get("door.expire_day") == null) {
            valid = false;
        }
        if (config.get("door.database.type") == null) {
            valid = false;
        }
        if (!valid) {
            plugin.saveResource("config.yml", true);
            config = YamlConfiguration.loadConfiguration(configFile);

            Log.logInfo("The configuration is not set correctly. The default configuration has been restored.");
        }
    }

    public void saveConfig() {
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}