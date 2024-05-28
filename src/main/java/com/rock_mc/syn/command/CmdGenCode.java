package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.utlis.Utils;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.log.LogProvider;
import org.bukkit.entity.Player;

public class CmdGenCode {

    private final static String commandName = CmdManager.GENCODE;

    public static void run(Syn plugin, LogProvider log, Player player, String[] args) {
        if (plugin.lacksPermission(player, commandName)) {
            log.sendMessage(player, "You don't have permission to use this command.");
            return;
        }

        // args[1] = codeNum
        int codeNum = 1;
        if (args.length == 2) {
            try {
                codeNum = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.sendMessage(player, plugin.cmdManager.getCmd(commandName).usage);
                return;
            }
            if (codeNum < 1) {
                log.sendMessage(player, "The codeNum must be greater than 0.");
                return;
            }
            if (codeNum > 1000) {
                log.sendMessage(player, "The codeNum must be less than 1000.");
                return;
            }
        }

        String available_characters = plugin.getConfig().getString(Config.AVAILABLE_CHARS);
        int code_length = plugin.getConfig().getInt(Config.CODE_LENGTH);

        // Generate a verification code
        // Check the code is unique
        StringBuilder msg = new StringBuilder();

        for (int i = 0; i < codeNum; i++) {

            String code = Utils.generateCode(available_characters, code_length);
            while (plugin.dbManager.containsCode(code)) {
                code = Utils.generateCode(available_characters, code_length);
            }
            plugin.dbManager.addCode(code);

            if (player == null) {
                if (!msg.isEmpty()) {
                    msg.append(", ");
                }
                msg.append(code);
            } else {
                String showCodeUrl = plugin.getConfig().getString(Config.SHOW_CODE_URL);
                msg.append("\n");
                msg.append(showCodeUrl);
                msg.append(code);
            }
        }

        log.sendMessage(player, msg.toString().trim());

    }

}
