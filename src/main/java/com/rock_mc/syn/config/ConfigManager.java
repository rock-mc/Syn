package com.rock_mc.syn.config;

import com.rock_mc.syn.log.LogPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class ConfigManager {
    private final JavaPlugin plugin ;

    private static final LogPlugin LOG_PLUGIN = new LogPlugin();

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
        if (config.get(Config.GUEST) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.GUEST) instanceof Boolean)) {
                valid = false;
            }
        }

        if (config.get(Config.AVAILABLE_CHARS) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.AVAILABLE_CHARS) instanceof String)) {
                valid = false;
            }
            else if (Objects.requireNonNull(config.getString(Config.AVAILABLE_CHARS)).length() < 10) {
                valid = false;
            }
        }

        if (config.get(Config.CODE_LENGTH) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.CODE_LENGTH) instanceof Integer)) {
                valid = false;
            }
            else if (config.getInt(Config.CODE_LENGTH) < 6) {
                valid = false;
            }
            else if (config.getInt(Config.CODE_LENGTH) > 32) {
                valid = false;
            }
        }

        if (config.get(Config.EXPIRE_DAYS) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.EXPIRE_DAYS) instanceof Integer)) {
                valid = false;
            }
            else if (config.getInt(Config.EXPIRE_DAYS) < 0) {
                valid = false;
            }
        }

        if (config.get(Config.MAX_WAIT_INPUT_CODE_SECONDS) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.MAX_WAIT_INPUT_CODE_SECONDS) instanceof Integer)) {
                valid = false;
            }
            else if (config.getInt(Config.MAX_WAIT_INPUT_CODE_SECONDS) < 0) {
                valid = false;
            }
        }

        if (config.get(Config.CLEAR_DAYS) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.CLEAR_DAYS) instanceof Integer)) {
                valid = false;
            }
            else if (config.getInt(Config.CLEAR_DAYS) < 0) {
                valid = false;
            }
        }

        if (config.get(Config.DATABASE_TYPE) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.DATABASE_TYPE) instanceof String)) {
                valid = false;
            }
            else if (!"sqlite".equalsIgnoreCase(config.getString(Config.DATABASE_TYPE))) {
                valid = false;
            }
        }

        if (config.get(Config.SHOW_CODE_URL) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.SHOW_CODE_URL) instanceof String)) {
                valid = false;
            }
            else if (Objects.requireNonNull(config.getString(Config.SHOW_CODE_URL)).isEmpty()) {
                valid = false;
            }
            else {
                // Check if the URL is valid
                String url = config.getString(Config.SHOW_CODE_URL);
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    valid = false;
                }
            }
        }

        if (config.get(Config.MAX_INPUT_CODE_TIMES) == null) {
            valid = false;
        }
        else {
            if (!(config.get(Config.MAX_INPUT_CODE_TIMES) instanceof Integer)) {
                valid = false;
            }
            else if (config.getInt(Config.MAX_INPUT_CODE_TIMES) < 1) {
                valid = false;
            }
        }

        if (!valid) {
            plugin.saveResource("config.yml", true);
            config = YamlConfiguration.loadConfiguration(configFile);

            LOG_PLUGIN.logInfo("The configuration is not set correctly. The default configuration has been restored.");
        }
    }
}