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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log {
    private final static String commandName = CmdManager.LOG;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static void exec(Syn plugin, Logger logger, Player player, String[] args) {
        synchronized (Syn.apiLock) {
            // TODO: syn log t:[time] u:[player] p:[page]

            long[] times = Utils.parseTime(args);
            List<String> players = Utils.parseUsers(args);

            Timestamp start = null;
            Timestamp end = null;

            if (times.length == 2 && times[0] > 0) {
                start = new Timestamp(System.currentTimeMillis() - (times[0] * 1000));
                end = new Timestamp(System.currentTimeMillis() - (times[1] * 1000));
            }

            Map<String, String> playerNamesByUUID = new HashMap<>();

            for (String playerName : players) {
                PluginPlayerInfo pluginPlayerInfo = plugin.dbManager.getPlayerByName(playerName);
                playerNamesByUUID.put(pluginPlayerInfo.getPlayer_uuid(), pluginPlayerInfo.getPlayer_name());
            }

            List<EventLog> logEvents = plugin.dbManager.getLogEvents(playerNamesByUUID.values().stream().toList(), start, end);

            for (EventLog logEvent : logEvents) {
                String time = sdf.format(logEvent.getCreatedAt());
                String playerName = playerNamesByUUID.get(logEvent.getPlayerUUID());
                logger.sendMessage(player, String.format("時間: %s 事件: %-12s 玩家: %-12s", time, logEvent.getEventName(), playerName));
            }
            logger.sendMessage(player, "查詢:" + sdf.format(start) + " - " + sdf.format(end));

        }
    }
}
