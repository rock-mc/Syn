package com.rock_mc.securedoors.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public boolean isOpen() {
        return config.getBoolean("open");
    }

    public void setOpen(boolean open) {
        config.set("open", open);
        plugin.saveConfig();
    }
}
