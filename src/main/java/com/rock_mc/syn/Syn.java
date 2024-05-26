package com.rock_mc.syn;

import com.rock_mc.syn.command.CmdExecutor;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.ConfigManager;
import com.rock_mc.syn.db.DbManager;
import com.rock_mc.syn.event.DiscordListener;
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

    public CmdManager cmdManager;

    String ANSI_ART = """
███████╗██╗   ██╗███╗   ██╗
██╔════╝╚██╗ ██╔╝████╗  ██║
███████╗ ╚████╔╝ ██╔██╗ ██║
╚════██║  ╚██╔╝  ██║╚██╗██║
███████║   ██║   ██║ ╚████║
╚══════╝   ╚═╝   ╚═╝  ╚═══╝""";
    // ANSI Shadow

    @Override
    public void onEnable() {

        configManager = new ConfigManager(this);
        configManager.load();

        dbManager = new DbManager(this);
        dbManager.load();

        freezePlayerMap = new HashMap<>();
        cmdManager = new CmdManager();

        getServer().getPluginManager()
                .registerEvents(new EventListener(this), this);

        getServer().getPluginManager()
                .registerEvents(new DiscordListener(this), this);

        Objects.requireNonNull(getCommand(APP_NAME.toLowerCase()))
                .setExecutor(new CmdExecutor(this));

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        dbManager.save();
        dbManager.close();

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }
    }
}
