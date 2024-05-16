package com.rock_mc.securedoors;

import org.bukkit.plugin.java.JavaPlugin;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "InvitSys";

    @Override
    public void onEnable() {
        // Plugin startup logic

        Log.logger = getLogger();

        Log.server("Enable", "Active");

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("invits").setExecutor(new PlayerCommand());


        Log.server("Enable", "Complete");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
