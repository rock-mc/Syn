package com.rock_mc.syn.log;

public class LogManager {
    public static LogPlugin LOG_PLUGIN;
    public static LogDiscord LOG_DISCORD;

    public LogManager() {
        LOG_PLUGIN = new LogPlugin();
        LOG_DISCORD = new LogDiscord();
    }
}
