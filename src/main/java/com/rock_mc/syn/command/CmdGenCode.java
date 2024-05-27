package com.rock_mc.syn.command;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.Utils;
import com.rock_mc.syn.config.Config;
import org.jetbrains.annotations.NotNull;

public class CmdGenCode {

    public static @NotNull String run(Syn plugin, int codeNum, boolean hasUrl) {
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

            if (!hasUrl) {
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

        return msg.toString().trim();
    }

}
