package com.rock_mc.syn.command;

import java.util.HashMap;

public class CmdManager {
    private final HashMap<String, Command> cmdMap;
    
    public static String SYN = "syn";
    public static String VERIFY = "verify";
    public static String GENCODE = "gencode";
    public static String INFO = "info";
    public static String BAN = "ban";
    public static String UNBAN = "unban";
    public static String GUEST = "guest";
    public static String LOG = "log";
    
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

    public String [] getCmdList() {
        return cmdMap.keySet().stream().sorted().toArray(String[]::new);
    }
}
