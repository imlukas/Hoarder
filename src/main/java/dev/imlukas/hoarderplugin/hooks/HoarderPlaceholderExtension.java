package dev.imlukas.hoarderplugin.hooks;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.leaderboard.LeaderboardCache;
import dev.imlukas.hoarderplugin.utils.time.localdate.DateUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoarderPlaceholderExtension extends PlaceholderExpansion {
    private final LeaderboardCache leaderboardCache;
    private final HoarderPlugin plugin;

    public HoarderPlaceholderExtension(HoarderPlugin plugin) {
        this.leaderboardCache = plugin.getLeaderboardCache();
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hoarder";
    }

    @Override
    public @NotNull String getAuthor() {
        return "imlukas";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (params.equals(player.getName().toLowerCase() + "_sold")) {
                HoarderEvent currentEvent = (HoarderEvent) plugin.getEventTracker().getActiveEvent();

                if (currentEvent == null) {
                    return "0";
                }

                HoarderPlayerEventData playerData = currentEvent.getEventData().getPlayerData(onlinePlayer.getUniqueId());

                if (playerData == null) {
                    return "0";
                }

                return String.valueOf(playerData.getSoldItems());
            }
        }

        if (params.equals("time_left")) {
            return DateUtil.formatDuration(plugin.getTimeLeft());
        }

        for (int i = 1; i <= 10; i++) {
            if (params.equals("top" + i + "_sold")) {

                if (leaderboardCache.getStats(i) == null) {
                    return "No player found on this position.";
                }

                return leaderboardCache.getStats(i).getSoldItems() + "";
            }

            if (params.equals("top" + i)) {

                if (leaderboardCache.getStats(i) == null) {
                    return "No player found on this position.";
                }

                return leaderboardCache.getStats(i).getPlayer() == null ? leaderboardCache.getStats(i).getOfflinePlayer().getName() : leaderboardCache.getStats(i).getPlayer().getName();
            }
        }

        return "";
    }
}
