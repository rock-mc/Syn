package com.rock_mc.syn.command;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class CmdManager {
    private final HashMap<String, Command> cmdMap;

    public static final String SYN = "syn";
    public static final String VERIFY = "verify";
    public static final String GENCODE = "gencode";
    public static final String INFO = "info";
    public static final String BAN = "ban";
    public static final String UNBAN = "unban";
    public static final String GUEST = "guest";
    public static final String LOG = "log";

    public CmdManager() {
        cmdMap = new HashMap<>();

        cmdMap.put("syn", new Command("syn", "Show help of Syn", "/syn", null));
        cmdMap.put("verify", new Command("verify", "Input the verification code to verify player", "/syn verify <code>", Permission.VERIFY));
        cmdMap.put("gencode", new Command("gencode", "Generate verification code", "/syn gencode [number]", Permission.GENCODE));
        cmdMap.put("info", new Command("info", "Show the the state of Syn System info", "/syn info", Permission.INFO));
        cmdMap.put("ban", new Command("ban", "Ban the player", "/syn ban player [day hour min sec]", Permission.BAN));
        cmdMap.put("unban", new Command("unban", "Unban the player", "/syn unban player", Permission.UNBAN));
        cmdMap.put("guest", new Command("guest", "Allow the player in the allowlist to come into the server.", "/syn guest", Permission.GUEST));
        cmdMap.put("log", new Command("log", "Show the log of the player", "/syn log [time] [player] [page]", Permission.LOG));

    }

    public Command getCmd(String name) {
        return cmdMap.get(name);
    }

    public String[] getCmdList() {
        return cmdMap.keySet().stream().sorted().toArray(String[]::new);
    }

    public boolean lacksPermission(Player player, String command) {

        if (player == null) {
            // The console has all permissions
            return false;
        }

        return !player.hasPermission(getCmd(command).permission);
    }
}
