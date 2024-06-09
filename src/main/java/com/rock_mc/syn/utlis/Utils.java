package com.rock_mc.syn.utlis;

import com.google.common.primitives.Ints;
import com.rock_mc.syn.command.CmdManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    // 與 coreprotect 相同輸入時間方式 https://github.com/PlayPro/CoreProtect/blob/ca59ff25dfa5abf9b4f0537255a89ba8a3511db5/src/main/java/net/coreprotect/command/CommandHandler.java#L684
    public static long[] parseTime(String[] inputArguments) {
        String[] argumentArray = inputArguments.clone();
        long timeStart = 0;
        long timeEnd = 0;
        int count = 0;
        int next = 0;
        boolean range = false;
        double w = 0;
        double d = 0;
        double h = 0;
        double m = 0;
        double s = 0;
        for (String argument : argumentArray) {
            if (count > 0) {
                argument = argument.trim().toLowerCase(Locale.ROOT);
                argument = argument.replaceAll("\\\\", "");
                argument = argument.replaceAll("'", "");

                if (argument.equals("t:") || argument.equals("time:")) {
                    next = 1;
                } else if (next == 1 || argument.startsWith("t:") || argument.startsWith("time:")) {
                    // time arguments
                    argument = argument.replaceAll("time:", "");
                    argument = argument.replaceAll("t:", "");
                    argument = argument.replaceAll("y", "y:");
                    argument = argument.replaceAll("m", "m:");
                    argument = argument.replaceAll("w", "w:");
                    argument = argument.replaceAll("d", "d:");
                    argument = argument.replaceAll("h", "h:");
                    argument = argument.replaceAll("s", "s:");
                    range = argument.contains("-");

                    int argCount = 0;
                    String[] i2 = argument.split(":");
                    for (String i3 : i2) {
                        if (range && argCount > 0 && timeStart == 0 && i3.startsWith("-")) {
                            timeStart = (long) (((w * 7 * 24 * 60 * 60) + (d * 24 * 60 * 60) + (h * 60 * 60) + (m * 60) + s));
                            w = 0;
                            d = 0;
                            h = 0;
                            m = 0;
                            s = 0;
                        }

                        if (i3.endsWith("w") && w == 0) {
                            String i4 = i3.replaceAll("[^0-9.]", "");
                            if (i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0 && i4.indexOf('.') == i4.lastIndexOf('.')) {
                                w = Double.parseDouble(i4);
                            }
                        } else if (i3.endsWith("d") && d == 0) {
                            String i4 = i3.replaceAll("[^0-9.]", "");
                            if (i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0 && i4.indexOf('.') == i4.lastIndexOf('.')) {
                                d = Double.parseDouble(i4);
                            }
                        } else if (i3.endsWith("h") && h == 0) {
                            String i4 = i3.replaceAll("[^0-9.]", "");
                            if (i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0 && i4.indexOf('.') == i4.lastIndexOf('.')) {
                                h = Double.parseDouble(i4);
                            }
                        } else if (i3.endsWith("m") && m == 0) {
                            String i4 = i3.replaceAll("[^0-9.]", "");
                            if (i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0 && i4.indexOf('.') == i4.lastIndexOf('.')) {
                                m = Double.parseDouble(i4);
                            }
                        } else if (i3.endsWith("s") && s == 0) {
                            String i4 = i3.replaceAll("[^0-9.]", "");
                            if (i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0 && i4.indexOf('.') == i4.lastIndexOf('.')) {
                                s = Double.parseDouble(i4);
                            }
                        }

                        argCount++;
                    }
                    if (timeStart > 0) {
                        timeEnd = (long) (((w * 7 * 24 * 60 * 60) + (d * 24 * 60 * 60) + (h * 60 * 60) + (m * 60) + s));
                    } else {
                        timeStart = (long) (((w * 7 * 24 * 60 * 60) + (d * 24 * 60 * 60) + (h * 60 * 60) + (m * 60) + s));
                    }
                    next = 0;
                } else {
                    next = 0;
                }
            }
            count++;
        }

        if (timeEnd >= timeStart) {
            return new long[]{timeEnd, timeStart};
        } else {
            return new long[]{timeStart, timeEnd};
        }
    }

    // 與 coreprotect 相同輸入玩家方式
    public static List<String> parseUsers(String[] inputArguments) {
        String[] argumentArray = inputArguments.clone();
        List<String> users = new ArrayList<>();
        System.out.println("===============");
        for (int i=0;i<inputArguments.length;i++){
            System.out.println(i+" "+inputArguments[i]);
        }
        System.out.println("===============");
        int count = 0;
        int next = 0;
        for (String argument : argumentArray) {
            if (count > 0) {
                argument = argument.trim().toLowerCase(Locale.ROOT);
                argument = argument.replaceAll("\\\\", "");
                argument = argument.replaceAll("'", "");

                if (next == 2) {
                    if (argument.endsWith(",")) {
                        next = 2;
                    }
                    else {
                        next = 0;
                    }
                }
                else if (argument.equalsIgnoreCase("p:") || argument.equals("user:") || argument.equals("users:") || argument.equals("u:")) {
                    next = 1;
                }
                else if (next == 1 || argument.startsWith("p:") || argument.startsWith("user:") || argument.startsWith("users:") || argument.startsWith("u:")) {
                    argument = argument.replaceAll("user:", "");
                    argument = argument.replaceAll("users:", "");
                    argument = argument.replaceAll("p:", "");
                    argument = argument.replaceAll("u:", "");
                    if (argument.contains(",")) {
                        String[] i2 = argument.split(",");
                        for (String i3 : i2) {
                            if (!users.contains(i3)) {
                                users.add(i3);
                            }
                            parseUser(users, i3);
                        }
                        if (argument.endsWith(",")) {
                            next = 1;
                        }
                        else {
                            next = 0;
                        }
                    }
                    else {
                        parseUser(users, argument);
                        next = 0;
                    }
                }
                else if (argument.endsWith(",") || argument.endsWith(":")) {
                    next = 2;
                }
                else if (argument.contains(":")) {
                    next = 0;
                }
                else {
                    parseUser(users, argument);
                    next = 0;
                }
            }
            count++;
        }
        return users;
    }

    public static Integer parsePage(String[] argumentArray) {

        for (String argument : argumentArray) {
            if(argument.startsWith("page:")){
                argument = argument.replaceFirst("page:", "");
            }

            if(Ints.tryParse(argument)!=null){
                return Ints.tryParse(argument);
            }
        }
        return 1;
    }

    private static void parseUser(List<String> users, String user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }
}
