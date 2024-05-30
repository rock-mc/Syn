package com.rock_mc.syn.log;

public class LogManager {
    public static LoggerPlugin LOG_PLUGIN;
    public static LoggerDiscord LOG_DISCORD;

    public LogManager() {
        LOG_PLUGIN = new LoggerPlugin();
        LOG_DISCORD = new LoggerDiscord();
    }
}
