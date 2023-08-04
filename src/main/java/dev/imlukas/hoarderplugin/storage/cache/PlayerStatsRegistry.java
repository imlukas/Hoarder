package dev.imlukas.hoarderplugin.storage.cache;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerStatsRegistry {

    private final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();

    public void register(PlayerStats playerStats) {
        System.out.println("Registered playerstats");
        System.out.println(playerStats.getPlayerId());
        System.out.println("Wins: " + playerStats.getWins());
        System.out.println("Sold: " + playerStats.getSoldItems());
        System.out.println("Top 3: " + playerStats.getTop3());
        playerStatsMap.put(playerStats.getPlayerId(), playerStats);
    }

    public PlayerStats getPlayerStats(UUID playerId) {
        return playerStatsMap.get(playerId);
    }

    public void unregister(UUID playerId) {
        System.out.println("Unregistered playerstats");
        System.out.println(playerId);
        System.out.println("Wins: " + playerStatsMap.get(playerId).getWins());
        System.out.println("Sold: " + playerStatsMap.get(playerId).getSoldItems());
        System.out.println("Top 3: " + playerStatsMap.get(playerId).getTop3());
        playerStatsMap.remove(playerId);
    }
}
