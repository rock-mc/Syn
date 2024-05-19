package com.rock_mc.securedoors;

import com.rock_mc.securedoors.config.ConfigManager;
import com.rock_mc.securedoors.db.DbManager;
import com.rock_mc.securedoors.event.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SecureDoors extends JavaPlugin {

    public static final String APP_NAME = "SecureDoors";

    public ConfigManager configManager;
    public DbManager dbManager;

    public HashMap<UUID, Location> freezePlayerMap;

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.dbManager = new DbManager(this);
        this.dbManager.load();

        this.freezePlayerMap = new HashMap<>();

        getServer().getPluginManager()
                .registerEvents(new EventListener(this), this);
        Objects.requireNonNull(this.getCommand("sd"))
                .setExecutor(new Command(this));

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
