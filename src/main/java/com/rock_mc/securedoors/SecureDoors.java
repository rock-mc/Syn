package com.rock_mc.securedoors;

import com.rock_mc.securedoors.commands.SdCommand;
import com.rock_mc.securedoors.listeners.EventListener;
import com.rock_mc.securedoors.utils.ConfigManager;
import com.rock_mc.securedoors.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "SecureDoors";

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        Log.logger = getLogger();

        configManager = new ConfigManager(this);


        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getCommand("sd").setExecutor(new SdCommand(this));

    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
