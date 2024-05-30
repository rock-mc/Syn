package com.rock_mc.syn.api;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

public class ApiManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ApiManager.class);
    private final Syn plugin;
    private final Object lock = new Object();

    // args[0] is the command name, such as "verify"
    // args[1..] are the arguments to the command

    public ApiManager(Syn plugin) {
        this.plugin = plugin;
    }

    public void help(Logger logger, Player player) {
        Help.exec(plugin, logger, player);
    }

    public boolean verify(Logger logger, Player player, String[] args) {
        if (args.length != 2) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.VERIFY).usage);
            return false;
        }

        return verify(logger, player, args[1]);
    }

    public boolean verify(Logger logger, Player player, String code) {
        synchronized (lock) {
            return Verify.exec(plugin, logger, player, code);
        }
    }

    public String[] genCode(Logger logger, Player player, String[] args) {
        int codeNum;
        if (args.length == 1) {
            codeNum = 1;
        } else if (args.length == 2) {
            try {
                codeNum = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.sendMessage(player, "Invalid number of codes.");
                logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.GENCODE).usage);
                return null;
            }
        } else {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.GENCODE).usage);
            return null;
        }

        return genCode(logger, player, codeNum);
    }

    public String[] genCode(Logger logger, Player player, int codeNum) {
        synchronized (lock) {
            return GenCode.exec(plugin, logger, player, codeNum);
        }
    }

    public boolean guest(Logger logger, Player player) {
        synchronized (lock) {
            return Guest.exec(plugin, logger, player);
        }
    }

    public boolean ban(Logger logger, Player player, String[] args) {

        if (args.length < 1) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.BAN).usage);
            return false;
        }
        if (args.length > 4) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.BAN).usage);
            return false;
        }

        String banPlayerName = args[1];
        String reason = "Admin Ban";
        long banSecs = 0L;
        if (args.length == 3) {
            // the parameter can be either the reason or the ban time
            String temp = args[2];

            if (Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                // it is a time string
                banSecs = Utils.strToTime(temp);
            } else {
                // it is a reason
                reason = temp;
            }
        }
        else if (args.length == 4) {
            String temp = args[2];

            if (Utils.isValidCode("0123456789ydhms", temp.length(), temp)) {
                // it is a time string
                banSecs = Utils.strToTime(temp);
                reason = args[3];
            } else {
                // it is a reason
                banSecs = Utils.strToTime(args[3]);
                reason = temp;
            }
        }

        return ban(logger, player, banPlayerName, reason, banSecs);
    }

    public boolean ban(Logger logger, Player player, String banPlayerName, String reason, Long banSecs) {
        synchronized (lock) {
            return Ban.exec(plugin, logger, player, banPlayerName, reason, banSecs);
        }
    }

    public boolean unban(Logger logger, Player player, String[] args) {
        if (args.length != 2) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.UNBAN).usage);
            return false;
        }

        String banPlayerName = args[1];

        return unban(logger, player, banPlayerName);
    }

    public boolean unban(Logger logger, Player player, String banPlayerName) {
        synchronized (lock) {
            return Unban.exec(plugin, logger, player, banPlayerName);
        }
    }

    public void info(Logger logger, Player player, String[] args) {
        if (args.length != 2) {
            logger.sendMessage(player, "Invalid number of arguments.");
            logger.sendMessage(player, plugin.cmdManager.getCmd(CmdManager.INFO).usage);
            return;
        }

        String playerName = args[1];

        info(logger, player, playerName);
    }

    public void info(Logger logger, Player player, String playerName) {
        synchronized (lock) {
            Info.exec(plugin, logger, player, playerName);
        }
    }
}
