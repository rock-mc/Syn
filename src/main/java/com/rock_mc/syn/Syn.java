package com.rock_mc.syn;

import com.rock_mc.syn.bstats.Metrics;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.ConfigManager;
import com.rock_mc.syn.db.DbManager;
import com.rock_mc.syn.event.CmdExecutor;
import com.rock_mc.syn.event.DiscordListener;
import com.rock_mc.syn.event.EventListener;
import com.rock_mc.syn.log.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Syn extends JavaPlugin {

    public static final String APP_NAME = "Syn";

    public ConfigManager configManager;
    public DbManager dbManager;

    public ConcurrentHashMap<UUID, Location> freezePlayerMap;

    public CmdManager cmdManager;
    public LogManager logManager;

    @Deprecated // No longer used - DB operations are protected by DbManager.dbLock
    public static final Object apiLock = new Object();
    public boolean isFolia;

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

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }

        try {
            configManager = new ConfigManager(this);
            configManager.load();

            dbManager = new DbManager(this);
            dbManager.load();

        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        freezePlayerMap = new ConcurrentHashMap<>();
        cmdManager = new CmdManager();
        logManager = new LogManager();

        getServer().getPluginManager()
                .registerEvents(new EventListener(this), this);

        try {
            Class.forName("github.scarsz.discordsrv.DiscordSRV");

            getServer().getPluginManager()
                    .registerEvents(new DiscordListener(this), this);

            // Maybe we can do more verification here
            // Such as checking the channel that the bot is listening to?

            Bukkit.getLogger().info("DiscordSRV is enabled.");
        } catch (ClassNotFoundException e) {
            // DiscordSRV is not enabled.
        }

        Objects.requireNonNull(getCommand(CmdManager.SYN))
                .setExecutor(new CmdExecutor(this));

        Metrics metrics = new Metrics(this, 22307);

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }

        this.dbManager.addLogEvent("server", "start");
    }

    @Override
    public void onDisable() {
        saveConfig();
        this.dbManager.addLogEvent("server", "stop");
        dbManager.save();
        dbManager.close();

        for (String line : ANSI_ART.split("\n")) {
            Bukkit.getLogger().info(line);
        }
        // show plugin version
        Bukkit.getLogger().info("Plugin " + APP_NAME + " v" + getDescription().getVersion() + " is disabled.");
    }
}
