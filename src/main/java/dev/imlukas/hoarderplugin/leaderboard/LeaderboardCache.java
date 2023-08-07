package dev.imlukas.hoarderplugin.leaderboard;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.sql.SQLHandler;
import dev.imlukas.hoarderplugin.utils.collection.MapUtils;

import java.util.*;

public class LeaderboardCache {

    private final SQLHandler sqlHandler;
    private final Map<Integer, PlayerStats> lastWinnersCached = new HashMap<>();
    private final Map<Integer, PlayerStats> top10Cached = new HashMap<>();

    public LeaderboardCache(HoarderPlugin plugin) {
        sqlHandler = plugin.getSQLHandler();
        sqlHandler.fetchEventStats().thenAccept(this::update);
    }

    public void update(Map<UUID, PlayerStats> fullLeaderboard) {
        Map<PlayerStats, Integer> scoreMap = new HashMap<>();

        List<PlayerStats> values = new ArrayList<>(fullLeaderboard.values());

        int iterationAmount = Math.min(10, values.size());

        for (int i = 0; i < iterationAmount; i++) {
            PlayerStats playerStats = values.get(i);

            if (playerStats == null) {
                break;
            }

            scoreMap.put(playerStats, playerStats.getWins());
        }

        Map<Integer, PlayerStats> top10 = MapUtils.getLeaderboardMap(scoreMap);
        top10Cached.clear();
        top10Cached.putAll(top10);

        Map<Integer, PlayerStats> lastWinners = new HashMap<>();
        sqlHandler.fetchLastWinners().thenAccept((winners) -> {
            int i = 1;

            for (UUID winner : winners) {
                lastWinners.put(i, fullLeaderboard.get(winner));
                i++;
            }

            lastWinnersCached.clear();
            lastWinnersCached.putAll(lastWinners);
        });
    }

    public PlayerStats get(int position) {

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
