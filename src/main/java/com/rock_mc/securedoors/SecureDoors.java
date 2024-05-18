package com.rock_mc.securedoors;

import com.rock_mc.securedoors.commands.Command;
import com.rock_mc.securedoors.listeners.EventListener;
import com.rock_mc.securedoors.config.ConfigManager;
import com.rock_mc.securedoors.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "SecureDoors";

    private ConfigManager configManager;



    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getCommand("sd").setExecutor(new Command(this));

        Log.logInfo("Load success");
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

}
