package com.rock_mc.syn;

import com.rock_mc.syn.event.CmdExecutor;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.config.ConfigManager;
import com.rock_mc.syn.db.DbManager;
import com.rock_mc.syn.event.DiscordListener;
import com.rock_mc.syn.event.EventListener;
import com.rock_mc.syn.log.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
    public LogManager logManager;

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
        dbManager = new DbManager(this);

        try {
            configManager.load();
            dbManager.load();
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        freezePlayerMap = new HashMap<>();
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
