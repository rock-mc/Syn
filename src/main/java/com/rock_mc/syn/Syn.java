package com.rock_mc.syn;

import com.rock_mc.syn.config.ConfigManager;
import com.rock_mc.syn.db.DbManager;
import com.rock_mc.syn.event.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Syn extends JavaPlugin {

    public static final String APP_NAME = "Syn";

    public ConfigManager configManager;
    public DbManager dbManager;

    public HashMap<UUID, Location> freezePlayerMap;

    String ANSI_ART = """
███████╗██╗   ██╗███╗   ██╗
██╔════╝╚██╗ ██╔╝████╗  ██║
███████╗ ╚████╔╝ ██╔██╗ ██║
╚════██║  ╚██╔╝  ██║╚██╗██║
███████║   ██║   ██║ ╚████║
╚══════╝   ╚═╝   ╚═╝  ╚═══╝""" + " v " + this.getDescription().getVersion();
    // ANSI Shadow

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.dbManager = new DbManager(this);
        this.dbManager.load();

        this.freezePlayerMap = new HashMap<>();

        getServer().getPluginManager()
                .registerEvents(new EventListener(this), this);
        Objects.requireNonNull(this.getCommand(APP_NAME.toLowerCase()))
                .setExecutor(new Command(this));

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        this.dbManager.save();
        this.dbManager.close();

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }
    }
}
