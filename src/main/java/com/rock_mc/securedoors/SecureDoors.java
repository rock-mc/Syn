package com.rock_mc.securedoors;

import com.rock_mc.securedoors.db.DbManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "SecureDoors";

    public ConfigManager configManager;
    public DbManager dbManager;

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.dbManager = new DbManager(this, this.configManager);
        this.dbManager.load();

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Objects.requireNonNull(this.getCommand("sd"))
                .setExecutor(new Command(this, this.dbManager));

        Bukkit.getLogger().info("███████╗███████╗ ██████╗██╗   ██╗██████╗ ███████╗    ██████╗  ██████╗  ██████╗ ██████╗ ███████╗");
        Bukkit.getLogger().info("██╔════╝██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝    ██╔══██╗██╔═══██╗██╔═══██╗██╔══██╗██╔════╝");
        Bukkit.getLogger().info("███████╗█████╗  ██║     ██║   ██║██████╔╝█████╗      ██║  ██║██║   ██║██║   ██║██████╔╝███████╗");
        Bukkit.getLogger().info("╚════██║██╔══╝  ██║     ██║   ██║██╔══██╗██╔══╝      ██║  ██║██║   ██║██║   ██║██╔══██╗╚════██║");
        Bukkit.getLogger().info("███████║███████╗╚██████╗╚██████╔╝██║  ██║███████╗    ██████╔╝╚██████╔╝╚██████╔╝██║  ██║███████║");
        Bukkit.getLogger().info("╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚══════╝");

        Bukkit.getLogger().info("SecureDoors v " + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        this.dbManager.save();
        this.dbManager.close();

        Bukkit.getLogger().info("███████╗███████╗ ██████╗██╗   ██╗██████╗ ███████╗    ██████╗  ██████╗  ██████╗ ██████╗ ███████╗");
        Bukkit.getLogger().info("██╔════╝██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝    ██╔══██╗██╔═══██╗██╔═══██╗██╔══██╗██╔════╝");
        Bukkit.getLogger().info("███████╗█████╗  ██║     ██║   ██║██████╔╝█████╗      ██║  ██║██║   ██║██║   ██║██████╔╝███████╗");
        Bukkit.getLogger().info("╚════██║██╔══╝  ██║     ██║   ██║██╔══██╗██╔══╝      ██║  ██║██║   ██║██║   ██║██╔══██╗╚════██║");
        Bukkit.getLogger().info("███████║███████╗╚██████╗╚██████╔╝██║  ██║███████╗    ██████╔╝╚██████╔╝╚██████╔╝██║  ██║███████║");
        Bukkit.getLogger().info("╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚══════╝");

        Bukkit.getLogger().info("SecureDoors v " + this.getDescription().getVersion() + " has been disabled!");
    }
}
