
package com.rock_mc.syn.event;

import com.rock_mc.syn.api.Ban;
import com.rock_mc.syn.event.pluginevent.KickEvent;
import com.rock_mc.syn.log.LoggerPlugin;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class WaitVerify extends Thread {

    private final Syn plugin;
    private static final LoggerPlugin LOG_PLUGIN = new LoggerPlugin();
    private final Player player;
    private final float CHECK_TIME = 0.1F;
    private int MAX_WAIT_INPUT_CODE_SECONDS = 0;
    private int MAX_INPUT_CODE_TIMES = 0;

    public WaitVerify(Syn plugin, Player newPlayer) {
        this.plugin = plugin;
        this.player = newPlayer;

        this.MAX_WAIT_INPUT_CODE_SECONDS = plugin.getConfig().getInt(Config.MAX_WAIT_INPUT_CODE_SECONDS);
        this.MAX_INPUT_CODE_TIMES = plugin.getConfig().getInt(Config.MAX_INPUT_CODE_TIMES);
    }

    @Override
    public void run() {
        LOG_PLUGIN.sendMessage(player, "請在 " + MAX_WAIT_INPUT_CODE_SECONDS + " 秒內輸入驗證碼");

        int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
        LOG_PLUGIN.sendMessage(player, "您有 " + (MAX_INPUT_CODE_TIMES - (failTime - 1)) + " 次輸入機會");

        long sleepTime = (long) (1000 * CHECK_TIME);
        for (int i = 0; i * CHECK_TIME < MAX_WAIT_INPUT_CODE_SECONDS; i++) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!plugin.freezePlayerMap.containsKey(player.getUniqueId())) {
                break;
            }

            if (!player.isOnline()) {
                break;
            }
        }

        if (player.isOnline()) {
            String kickMsg;
            if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {

                Event event;

                failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
                if (failTime >= MAX_INPUT_CODE_TIMES) {

                    int banDays = plugin.getConfig().getInt(Config.INPUT_CODE_BAN_DAYS);

                    Ban.exec(plugin, LOG_PLUGIN, null, player.getName(), "7d", "請取得驗證碼後，參考官網輸入驗證碼方式，伺服器暫時凍結登入");

                    plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), 1);
                } else {
                    plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);

                    kickMsg = "未通過認證，請取得驗證碼後，參考官網教學輸入驗證碼";
                    event = new KickEvent(true, player, kickMsg);
                    Bukkit.getPluginManager().callEvent(event);
                }

                LOG_PLUGIN.broadcast(player.getDisplayName() + " 沒有正確回答女神 " + ChatColor.RED + Syn.APP_NAME + ChatColor.WHITE + " 的問題，被請出伺服器了...");
            }
        }
        plugin.freezePlayerMap.remove(player.getUniqueId());
    }
}

