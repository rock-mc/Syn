package com.rock_mc.syn.event;

import com.rock_mc.syn.Syn;
import com.rock_mc.syn.api.Ban;
import com.rock_mc.syn.config.Config;
import com.rock_mc.syn.event.pluginevent.PluginEventSender;
import com.rock_mc.syn.log.LoggerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitVerify extends BukkitRunnable {

    private final Syn plugin;
    private static final LoggerPlugin LOG_PLUGIN = new LoggerPlugin();
    private final Player player;
    private static final long CHECK_INTERVAL_TICKS = 2L; // ~100ms
    private final int MAX_WAIT_INPUT_CODE_SECONDS;
    private final int MAX_INPUT_CODE_TIMES;
    private int ticksElapsed = 0;

    public WaitVerify(Syn plugin, Player newPlayer) {
        this.plugin = plugin;
        this.player = newPlayer;

        this.MAX_WAIT_INPUT_CODE_SECONDS = plugin.getConfig().getInt(Config.MAX_WAIT_INPUT_CODE_SECONDS);
        this.MAX_INPUT_CODE_TIMES = plugin.getConfig().getInt(Config.MAX_INPUT_CODE_TIMES);
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, CHECK_INTERVAL_TICKS);

        String gn = plugin.configManager.getConfig().getString(Config.GODDESS_NAME);
        LOG_PLUGIN.sendMessage(player, gn + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 正守護著通往磐石的入口。她雙目炯炯有神，手持大斧，氣宇軒昂。世人若想進入磐石，就必須通過她的考驗與允許。");
        LOG_PLUGIN.sendMessage(player, gn + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 發現有未經許可的闖入者！她揮動大斧施展了封印之術，你的" + ChatColor.RED + "視野被黑暗吞噬" + ChatColor.RESET + "，" + ChatColor.RED + "雙腳被大地束縛" + ChatColor.RESET + "，動彈不得。");
        LOG_PLUGIN.sendMessage(player, gn + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 問道：「你是誰？你有創世神給你的 " + ChatColor.BOLD + "" + ChatColor.GOLD + "驗證碼" + ChatColor.RESET + " 嗎？」");

        int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
        LOG_PLUGIN.sendMessage(player, gn + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + "：「最好趕快回答，你有 " + (MAX_INPUT_CODE_TIMES - failTime) + " 次機會。」");
    }

    @Override
    public void run() {
        ticksElapsed += CHECK_INTERVAL_TICKS;

        // Player verified or disconnected — stop
        if (!plugin.freezePlayerMap.containsKey(player.getUniqueId()) || !player.isOnline()) {
            cancel();
            onTimeout();
            return;
        }

        // Time's up
        float secondsElapsed = ticksElapsed / 20.0f;
        if (secondsElapsed >= MAX_WAIT_INPUT_CODE_SECONDS) {
            cancel();
            onTimeout();
        }
    }

    private void onTimeout() {
        if (player.isOnline()) {
            if (!plugin.dbManager.isPlayerInAllowList(player.getUniqueId().toString())) {

                int failTime = plugin.dbManager.getFailedAttempts(player.getUniqueId().toString());
                if (failTime >= MAX_INPUT_CODE_TIMES) {

                    String banTimes = plugin.getConfig().getString(Config.INPUT_CODE_BAN_TIME);

                    Ban.exec(plugin, LOG_PLUGIN, null, player.getName(), "請取得驗證碼後，參考官網輸入驗證碼方式，伺服器暫時凍結登入", banTimes);
                } else {
                    plugin.dbManager.updateFailedAttempts(player.getUniqueId().toString(), failTime + 1);

                    PluginEventSender.sendKickEvent(player, "未通過認證，請取得驗證碼後，參考官網教學輸入驗證碼");
                }

                String gn = plugin.configManager.getConfig().getString(Config.GODDESS_NAME);
                LOG_PLUGIN.broadcast(player.getDisplayName() + " 沒有正確回答" + gn + " " + ChatColor.GOLD + Syn.APP_NAME + ChatColor.RESET + " 的問題，被請出伺服器了...");
            }
        }
        plugin.freezePlayerMap.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }
}
