package com.rock_mc.syn.utlis;

import com.rock_mc.syn.command.CmdManager;

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

    public static String timeToStr(long expiryTime) {
        long day = expiryTime / DAY;
        expiryTime %= DAY;
        long hour = expiryTime / HOUR;
        expiryTime %= HOUR;
        long min = expiryTime / MIN;
        expiryTime %= MIN;
        long sec = expiryTime;

        return timeToStr(day, hour, min, sec);
    }

    public static String timeToStr(long day, long hour, long min, long sec) {
        String result = null;
        if (day > 0) {
            result = day + " 天";
        }
        if (hour > 0) {
            if (result == null) {
                result = hour + " 小時";
            } else {
                result += " " + hour + " 小時";
            }
        }
        if (min > 0) {
            if (result == null) {
                result = min + " 分鐘";
            } else {
                result += " " + min + " 分鐘";
            }
        }
        if (sec > 0) {
            if (result == null) {
                result = sec + " 秒";
            } else {
                result += " " + sec + " 秒";
            }
        }
        if (result == null) {
            result = "∞";
        }
        return result;
    }

    public static long strToTime(String timeStr) {
        // timeStr is in the format of xyxdxhxmxs
        // x is a number
        // y is a letter, y, d, h, m, s
        // xy is the number of years
        // xd is the number of days
        // xh is the number of hours
        // xm is the number of minutes
        // xs is the number of seconds

        if (!isValidCode("0123456789ydhms", timeStr.length(), timeStr)) {
            return -1;
        }

        long time = 0;

        // Extract years
        int yearIndex = timeStr.indexOf('y');
        if (yearIndex != -1) {
            int years = Integer.parseInt(timeStr.substring(0, yearIndex));
            time += years * 365L * 24 * 60 * 60;
            timeStr = timeStr.substring(yearIndex + 1);
        }

        // Extract days
        int dayIndex = timeStr.indexOf('d');
        if (dayIndex != -1) {
            int days = Integer.parseInt(timeStr.substring(0, dayIndex));
            time += days * 24L * 60 * 60;
            timeStr = timeStr.substring(dayIndex + 1);
        }

        // Extract hours
        int hourIndex = timeStr.indexOf('h');
        if (hourIndex != -1) {
            int hours = Integer.parseInt(timeStr.substring(0, hourIndex));
            time += hours * 60L * 60;
            timeStr = timeStr.substring(hourIndex + 1);
        }

        // Extract minutes
        int minuteIndex = timeStr.indexOf('m');
        if (minuteIndex != -1) {
            int minutes = Integer.parseInt(timeStr.substring(0, minuteIndex));
            time += minutes * 60L;
            timeStr = timeStr.substring(minuteIndex + 1);
        }

        // Extract seconds
        if (!timeStr.isEmpty()) {
            int seconds = Integer.parseInt(timeStr.substring(0, timeStr.length() - 1));
            time += seconds;
        }
        return time;
    }

    public static boolean isCommand(String commandline, String commandCode) {
        if (commandline == null || commandCode == null) {
            return false;
        }
        return (commandline.startsWith(CmdManager.SYN + " " + commandCode));
    }

    public static String extractCommand(String[] cmdList, String cmdLine) {

        if (cmdLine == null) {
            return null;
        }

        cmdLine = cmdLine.trim().toLowerCase();

        if (!cmdLine.startsWith(CmdManager.SYN)) {
            return null;
        }

        for (String cmdCode : cmdList) {
            if (cmdLine.startsWith(CmdManager.SYN + " " + cmdCode.toLowerCase())) {
                return cmdCode;
            }
        }

        // starts with "syn" but not found in the command list
        return "";
    }
}
