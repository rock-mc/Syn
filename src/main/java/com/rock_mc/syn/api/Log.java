package com.rock_mc.syn.api;

import com.google.common.collect.Lists;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.command.CmdManager;
import com.rock_mc.syn.db.EventLog;
import com.rock_mc.syn.db.PluginPlayerInfo;
import com.rock_mc.syn.log.Logger;
import com.rock_mc.syn.utlis.Utils;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log {
    private final static String commandName = CmdManager.LOG;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static void exec(Syn plugin, Logger logger, Player player, String[] args) {
        synchronized (Syn.apiLock) {
            long[] times = Utils.parseTime(args);
            List<String> players = Utils.parseUsers(args);

            Timestamp start = null;
            Timestamp end = null;
            Integer page = null;

            LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("UTC"));
            Instant now = currentDateTime.atZone(ZoneId.of("Asia/Taipei")).toInstant();

            if (times.length == 2 && times[0] > 0) {
                start = Timestamp.from(now.minusSeconds(times[0]));
                end = Timestamp.from(now.minusSeconds(times[1]));
            } else {
                start = Timestamp.from(now.minusSeconds(3L * 30 * 24 * 60 * 60));
                end = Timestamp.from(now);
            }
            page = Utils.parsePage(args);

            List<String> playerUUIDs = Lists.newArrayList();

            for (String playerName : players) {
                PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(playerName);
                if (pluginPlayerInfo != null) {
                    playerUUIDs.add(pluginPlayerInfo.getPlayer_uuid());
                }
            }

            List<EventLog> logEvents = plugin.dbManager.getLogEvents(playerUUIDs, start, end, page);

            for (EventLog logEvent : logEvents) {
                String time = sdf.format(logEvent.getCreatedAt());
                logger.sendMessage(player, String.format("時間: %s UTC 事件: %-12s 玩家: %-12s", time, logEvent.getEventName(), logEvent.getPlayerName()));
            }
            logger.sendMessage(player, "查詢:" + sdf.format(start) + " - " + sdf.format(end)+" 第"+page+"頁");

        }
    }
}
