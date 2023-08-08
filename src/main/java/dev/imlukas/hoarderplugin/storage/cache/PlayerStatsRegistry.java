package dev.imlukas.hoarderplugin.storage.cache;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerStatsRegistry {

    private final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();

    public void register(PlayerStats playerStats) {
        playerStatsMap.put(playerStats.getPlayerId(), playerStats);
    }

    public PlayerStats getPlayerStats(UUID playerId) {

        if (!playerStatsMap.containsKey(playerId)) {
            return null;
        }

        return playerStatsMap.get(playerId);
    }

    public void unregister(UUID playerId) {
        playerStatsMap.remove(playerId);
    }
}
