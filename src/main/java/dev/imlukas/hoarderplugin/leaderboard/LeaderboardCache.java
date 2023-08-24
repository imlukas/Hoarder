package dev.imlukas.hoarderplugin.leaderboard;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.sql.SQLHandler;
import dev.imlukas.hoarderplugin.utils.collection.MapUtils;

import java.util.*;

public class LeaderboardCache {

    private final SQLHandler sqlHandler;
    private Map<Integer, PlayerStats> lastWinnersCached = new HashMap<>();
    private Map<Integer, PlayerStats> top10Cached = new HashMap<>();

    public LeaderboardCache(HoarderPlugin plugin) {
        sqlHandler = plugin.getSqlHandler();
        sqlHandler.fetchEventStats().thenAccept(this::update);
    }

    public void update(Map<UUID, PlayerStats> fullLeaderboard) {
        Map<PlayerStats, Integer> scoreMap = new HashMap<>();

        List<PlayerStats> values = new ArrayList<>(fullLeaderboard.values());

        for (PlayerStats value : values) {
            scoreMap.put(value, value.getWins());
        }

        Map<Integer, PlayerStats> top10 = MapUtils.getLeaderboardMap(scoreMap, 10);
        top10Cached = new HashMap<>(top10);

        Map<Integer, PlayerStats> lastWinners = new HashMap<>();
        sqlHandler.fetchLastWinners().thenAccept(winners -> {
            int i = 1;

            for (UUID winner : winners) {
                lastWinners.put(i, fullLeaderboard.get(winner));
                i++;
            }

            lastWinnersCached = new HashMap<>(lastWinners);
        });
    }

    public PlayerStats getStats(int position) {

        if (top10Cached.size() < position) {
            return null;
        }

        return top10Cached.get(position);
    }

    public Map<Integer, PlayerStats> getLastWinners() {
        return lastWinnersCached;
    }

    public Map<UUID, PlayerStats> getTop(int positions) {
        Map<UUID, PlayerStats> top = new HashMap<>();

        int iterationAmount = Math.min(positions, top10Cached.size());
        for (int i = 1; i <= iterationAmount; i++) {
            top.put(top10Cached.get(i).getPlayerId(), top10Cached.get(i));
        }

        return top;
    }
}
