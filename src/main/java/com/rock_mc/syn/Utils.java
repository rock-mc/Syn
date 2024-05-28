package com.rock_mc.syn;

import com.rock_mc.syn.command.CmdManager;
import org.jetbrains.annotations.NotNull;

public class Utils {

    public static String generateCode(String available_characters, int code_length) {
        StringBuilder verification_code = new StringBuilder();
        for (int i = 0; i < code_length; i++) {
            int randomIndex = (int) (Math.random() * available_characters.length());
            char randomChar = available_characters.charAt(randomIndex);

            verification_code.append(randomChar);
        }

        return verification_code.toString();
    }

    public static boolean isValidCode(String available_characters, int code_length, String code) {
        if (code.length() != code_length) {
            return false;
        }

        for (int i = 0; i < code.length(); i++) {
            if (!available_characters.contains(String.valueOf(code.charAt(i)))) {
                return false;
            }
        }

        return true;
    }

    static final int SEC = 1;
    static final int MIN = 60 * SEC;
    static final int HOUR = 60 * MIN;
    static final int DAY = 24 * HOUR;

    public static String timeToStr(long expiryTime){
        long day = expiryTime / DAY;
        expiryTime %= DAY;
        long hour = expiryTime / HOUR;
        expiryTime %= HOUR;
        long min = expiryTime / MIN;
        expiryTime %= MIN;
        long sec = expiryTime;

        return timeToStr(day, hour, min, sec);
    }

    public static String timeToStr(long day, long hour, long min, long sec){
        String result = null;
        if(day > 0){
            result = day + " 天";
        }
        if(hour > 0){
            if (result == null){
                result = hour + " 小時";
            }
            else {
                result += " " + hour + " 小時";
            }
        }
        if(min > 0){
            if(result == null){
                result = min + " 分鐘";
            }
            else{
                result += " " + min + " 分鐘";
            }
        }
        if(sec > 0){
            if (result == null){
                result = sec + " 秒";
            }
            else{
                result += " " + sec + " 秒";
            }
        }
        if (result == null){
            result = "∞";
        }
        return result;
    }

    public static boolean matchCommand(String commandline, String commandCode) {
        if (commandline == null || commandCode == null) {
            return false;
        }
        return (commandline.contains(CmdManager.SYN + " " + commandCode));
    }

    public static @NotNull String[] extractArgs(String cmdCode, String cmdLine) {
        int index = cmdLine.lastIndexOf(CmdManager.SYN + " " + cmdCode);
        if (index == -1) {
            return cmdLine.split(" ");
        }
        cmdLine = cmdLine.substring(index + CmdManager.SYN.length() + 1);
        return cmdLine.split(" ");
    }
}
