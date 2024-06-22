package com.rock_mc.syn.api;

import com.google.common.collect.Lists;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.EventLog;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

public class Log {
    private final static String commandName = CmdManager.LOG;
    private final static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static void exec(Syn plugin, Logger logger, Player player, String[] args) {
        synchronized (Syn.apiLock) {
            long[] times = Utils.parseTime(args);
            List<String> players = Utils.parseUsers(args);

            Timestamp start = null;
            Timestamp end = null;
            Integer page = null;
            Integer rows = null;

            Instant now = Instant.now();

            if (times.length == 2 && times[0] > 0) {
                start = Timestamp.from(now.minusSeconds(times[0]));
                end = Timestamp.from(now.minusSeconds(times[1]));
            } else {
                start = Timestamp.from(now.minusSeconds(3L * 30 * 24 * 60 * 60));
                end = Timestamp.from(now);
            }
            Integer[] pageAndRows = Utils.parsePageAndRows(args);
            if(pageAndRows!=null){
                page = pageAndRows[0];
                rows = pageAndRows[1];
            }else {
                page = Utils.parsePage(args);
                rows = Utils.parseRows(args);
            }
            List<String> playerUUIDs = Lists.newArrayList();

            for (String playerName : players) {
                PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(playerName);
                if (pluginPlayerInfo != null) {
                    playerUUIDs.add(pluginPlayerInfo.getPlayer_uuid());
                }
            }
            
            Long totalNumber = plugin.dbManager.countLogEvent(playerUUIDs, start, end);
            Long totalPage = totalNumber%rows == 0 ? totalNumber/rows : (totalNumber/rows+1);
            List<EventLog> logEvents = plugin.dbManager.getLogEvents(playerUUIDs, start, end, page, rows);

            for (EventLog logEvent : logEvents) {
                String time = timeString(logEvent.getCreatedAt().toInstant());

                logger.sendMessage(player, String.format("%s 事件: %-12s 玩家: %-12s", time, logEvent.getEventName(), logEvent.getPlayerName()));
            }
            logger.sendMessage(player, "查詢:" + timeString(start.toInstant()) + " - " + timeString(end.toInstant()));
            logger.sendMessage(player, "總共"+totalNumber+"筆、一頁"+rows+"筆、共"+totalPage+"頁 當前第"+page+"頁");
        }
    }

    public static String timeString(Instant instant){
        return instant.atZone(TimeZone.getDefault().toZoneId()).format(df);
    }
}
