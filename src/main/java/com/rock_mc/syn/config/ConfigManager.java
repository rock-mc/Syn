package com.rock_mc.syn.config;

import com.rock_mc.syn.log.LoggerPlugin;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final JavaPlugin plugin;

    private static final LoggerPlugin LOG_PLUGIN = new LoggerPlugin();

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
        } else {
            if (!(config.get(Config.GUEST) instanceof Boolean)) {
                valid = false;
            }
        }

        if (config.get(Config.AVAILABLE_CHARS) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.AVAILABLE_CHARS) instanceof String)) {
                valid = false;
            } else if (Objects.requireNonNull(config.getString(Config.AVAILABLE_CHARS)).length() < 10) {
                valid = false;
            }
        }

        if (config.get(Config.CODE_LENGTH) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.CODE_LENGTH) instanceof Integer)) {
                valid = false;
            } else if (config.getInt(Config.CODE_LENGTH) < 6) {
                valid = false;
            } else if (config.getInt(Config.CODE_LENGTH) > 32) {
                valid = false;
            }
        }

        if (config.get(Config.EXPIRE_TIME) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.EXPIRE_TIME) instanceof String temp)) {
                valid = false;
            } else if (!Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                valid = false;
            }
        }

        if (config.get(Config.MAX_WAIT_INPUT_CODE_SECONDS) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.MAX_WAIT_INPUT_CODE_SECONDS) instanceof Integer)) {
                valid = false;
            } else if (config.getInt(Config.MAX_WAIT_INPUT_CODE_SECONDS) < 0) {
                valid = false;
            }
        }

        if (config.get(Config.INPUT_CODE_BAN_TIME) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.INPUT_CODE_BAN_TIME) instanceof String temp)) {
                valid = false;
            } else if (!Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                valid = false;
            }
        }

        if (config.get(Config.CLEAR_DAYS) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.CLEAR_DAYS) instanceof Integer)) {
                valid = false;
            } else if (config.getInt(Config.CLEAR_DAYS) < 0) {
                valid = false;
            }
        }

        if (config.get(Config.SHOW_CODE_URL) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.SHOW_CODE_URL) instanceof String)) {
                valid = false;
            } else if (Objects.requireNonNull(config.getString(Config.SHOW_CODE_URL)).isEmpty()) {
                valid = false;
            } else {
                // Check if the URL is valid
                String url = config.getString(Config.SHOW_CODE_URL);
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    valid = false;
                }
            }
        }

        if (config.get(Config.MAX_INPUT_CODE_TIMES) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.MAX_INPUT_CODE_TIMES) instanceof Integer)) {
                valid = false;
            } else if (config.getInt(Config.MAX_INPUT_CODE_TIMES) < 1) {
                valid = false;
            }
        }

        if (config.get(Config.WELCOME) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.WELCOME) instanceof List)) {
                valid = false;
            }
        }

        if (config.get(Config.CHANNEL_NAME) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.CHANNEL_NAME) instanceof String)) {
                valid = false;
            }
        }

        if (config.get(Config.DATABASE_TYPE) == null) {
            valid = false;
        } else {
            if (!(config.get(Config.DATABASE_TYPE) instanceof String)) {
                valid = false;
            } else if (!"sqlite".equalsIgnoreCase(config.getString(Config.DATABASE_TYPE))) {
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