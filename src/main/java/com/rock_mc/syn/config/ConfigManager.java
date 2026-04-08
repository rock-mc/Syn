package com.rock_mc.syn.config;

import com.rock_mc.syn.log.LoggerPlugin;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        // Load default config from resource
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream == null) {
            LOG_PLUGIN.logInfo("Default config.yml resource not found.");
            return;
        }
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));

        // Validate each key; if invalid or missing, reset to default
        boolean changed = false;

        if (!(config.get(Config.GUEST) instanceof Boolean)) {
            resetKey(Config.GUEST, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.AVAILABLE_CHARS) instanceof String)
                || Objects.requireNonNull(config.getString(Config.AVAILABLE_CHARS)).length() < 10) {
            resetKey(Config.AVAILABLE_CHARS, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.CODE_LENGTH) instanceof Integer)
                || config.getInt(Config.CODE_LENGTH) < 6
                || config.getInt(Config.CODE_LENGTH) > 32) {
            resetKey(Config.CODE_LENGTH, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.EXPIRE_TIME) instanceof String temp)
                || !Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
            resetKey(Config.EXPIRE_TIME, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.MAX_WAIT_INPUT_CODE_SECONDS) instanceof Integer)
                || config.getInt(Config.MAX_WAIT_INPUT_CODE_SECONDS) < 0) {
            resetKey(Config.MAX_WAIT_INPUT_CODE_SECONDS, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.INPUT_CODE_BAN_TIME) instanceof String temp)
                || !Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
            resetKey(Config.INPUT_CODE_BAN_TIME, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.CLEAR_DAYS) instanceof Integer)
                || config.getInt(Config.CLEAR_DAYS) < 0) {
            resetKey(Config.CLEAR_DAYS, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.SHOW_CODE_URL) instanceof String)
                || Objects.requireNonNull(config.getString(Config.SHOW_CODE_URL)).isEmpty()
                || (!config.getString(Config.SHOW_CODE_URL).startsWith("http://")
                    && !config.getString(Config.SHOW_CODE_URL).startsWith("https://"))) {
            resetKey(Config.SHOW_CODE_URL, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.MAX_INPUT_CODE_TIMES) instanceof Integer)
                || config.getInt(Config.MAX_INPUT_CODE_TIMES) < 1) {
            resetKey(Config.MAX_INPUT_CODE_TIMES, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.WELCOME) instanceof List)) {
            resetKey(Config.WELCOME, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.CHANNEL_NAME) instanceof String)) {
            resetKey(Config.CHANNEL_NAME, defaultConfig);
            changed = true;
        }

        if (!(config.get(Config.DATABASE_TYPE) instanceof String)
                || !"sqlite".equalsIgnoreCase(config.getString(Config.DATABASE_TYPE))) {
            resetKey(Config.DATABASE_TYPE, defaultConfig);
            changed = true;
        }

        if (changed) {
            try {
                config.save(configFile);
            } catch (Exception e) {
                LOG_PLUGIN.logInfo("Failed to save config.yml: " + e.getMessage());
            }
            LOG_PLUGIN.logInfo("Some configuration values were invalid or missing. Default values have been applied.");
        }
    }

    private void resetKey(String key, YamlConfiguration defaultConfig) {
        Object defaultValue = defaultConfig.get(key);
        config.set(key, defaultValue);
        LOG_PLUGIN.logInfo("Config key '" + key + "' was invalid or missing. Reset to default.");
    }
}