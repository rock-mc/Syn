package com.rock_mc.securedoor;

import com.rock_mc.securedoor.config.ConfigManager;
import com.rock_mc.securedoor.db.DbManager;
import com.rock_mc.securedoor.event.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SecureDoor extends JavaPlugin {

    public static final String APP_NAME = "SecureDoor";

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

        Bukkit.getLogger().info("███████╗███████╗ ██████╗██╗   ██╗██████╗ ███████╗    ██████╗  ██████╗  ██████╗ ██████╗ ");
        Bukkit.getLogger().info("██╔════╝██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝    ██╔══██╗██╔═══██╗██╔═══██╗██╔══██╗");
        Bukkit.getLogger().info("███████╗█████╗  ██║     ██║   ██║██████╔╝█████╗      ██║  ██║██║   ██║██║   ██║██████╔╝");
        Bukkit.getLogger().info("╚════██║██╔══╝  ██║     ██║   ██║██╔══██╗██╔══╝      ██║  ██║██║   ██║██║   ██║██╔══██╗");
        Bukkit.getLogger().info("███████║███████╗╚██████╗╚██████╔╝██║  ██║███████╗    ██████╔╝╚██████╔╝╚██████╔╝██║  ██║");
        Bukkit.getLogger().info("╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝");

        Bukkit.getLogger().info("SecureDoor v " + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        this.dbManager.save();
        this.dbManager.close();

        Bukkit.getLogger().info("███████╗███████╗ ██████╗██╗   ██╗██████╗ ███████╗    ██████╗  ██████╗  ██████╗ ██████╗ ");
        Bukkit.getLogger().info("██╔════╝██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝    ██╔══██╗██╔═══██╗██╔═══██╗██╔══██╗");
        Bukkit.getLogger().info("███████╗█████╗  ██║     ██║   ██║██████╔╝█████╗      ██║  ██║██║   ██║██║   ██║██████╔╝");
        Bukkit.getLogger().info("╚════██║██╔══╝  ██║     ██║   ██║██╔══██╗██╔══╝      ██║  ██║██║   ██║██║   ██║██╔══██╗");
        Bukkit.getLogger().info("███████║███████╗╚██████╗╚██████╔╝██║  ██║███████╗    ██████╔╝╚██████╔╝╚██████╔╝██║  ██║");
        Bukkit.getLogger().info("╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝");

        Bukkit.getLogger().info("SecureDoor v " + this.getDescription().getVersion() + " has been disabled!");
    }
}
