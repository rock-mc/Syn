package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.api.Ban;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.pluginevent.PluginEventSender;
import com.rock_mc.syn.log.LoggerPlugin;
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
        LOG_PLUGIN.sendMessage(player, "女神 " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 正守護著通往磐石的入口。她雙目炯炯有神，手持大斧，氣宇軒昂。世人若想進入磐石，就必須通過她的考驗與允許。");
        LOG_PLUGIN.sendMessage(player, "女神 " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 問道：「你是誰？你有創世神給你的 " + ChatColor.BOLD + "" + ChatColor.GOLD + "驗證碼" + ChatColor.RESET + " 嗎？」");

        int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
        LOG_PLUGIN.sendMessage(player, "女神 " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + "：「最好趕快回答，你有 " + (MAX_INPUT_CODE_TIMES - (failTime - 1)) + " 次機會。」");

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
            if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {

                failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
                if (failTime >= MAX_INPUT_CODE_TIMES) {

                    String banTimes = plugin.getConfig().getString(Config.INPUT_CODE_BAN_TIME);

                    Ban.exec(plugin, LOG_PLUGIN, null, player.getName(), "請取得驗證碼後，參考官網輸入驗證碼方式，伺服器暫時凍結登入", banTimes);
                } else {
                    plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);

                    PluginEventSender.sendKickEvent(player, "未通過認證，請取得驗證碼後，參考官網教學輸入驗證碼");
                }

                LOG_PLUGIN.broadcast(player.getDisplayName() + " 沒有正確回答女神 " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 的問題，被請出伺服器了...");
            }
        }
        plugin.freezePlayerMap.remove(player.getUniqueId());
    }
}

