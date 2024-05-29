
package com.rock_mc.syn.event;

import com.rock_mc.syn.event.pluginevent.KickEvent;
import com.rock_mc.syn.log.LogPlugin;
import com.rock_mc.syn.Syn;
import com.rock_mc.syn.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class WaitVerify extends Thread {

    private final Syn plugin;
    private static final LogPlugin LOG_PLUGIN = new LogPlugin();
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
            String eventMessage;
            if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {
                eventMessage = "未通過認證，請取得驗證碼後，參考官網教學輸入驗證碼";
                Event event = new KickEvent(true, player, eventMessage);

                failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
                plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);

                LOG_PLUGIN.broadcast(player.getDisplayName() + " 沒有通過驗證，被請出伺服器了...");
                Bukkit.getPluginManager().callEvent(event);
            }
        }
        plugin.freezePlayerMap.remove(player.getUniqueId());
    }
}

