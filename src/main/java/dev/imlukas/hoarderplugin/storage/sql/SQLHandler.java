package dev.imlukas.hoarderplugin.storage.sql;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLHandler {

    private final PlayerStatsRegistry playerStatsRegistry;
    private final SQLDatabase sqlDatabase;

    public SQLHandler(HoarderPlugin plugin) {
        this.playerStatsRegistry = plugin.getPlayerStatsRegistry();
        sqlDatabase = plugin.getSqlDatabase();
    }

    public CompletableFuture<Map<UUID, PlayerStats>> fetchEventStats() {
        Map<UUID, PlayerStats> stats = new HashMap<>();
        return sqlDatabase.getOrCreateTable("hoarder_stats").executeQuery("SELECT * FROM hoarder_stats").thenApply(result -> {
            try {
                while (result.next()) {
                    UUID playerId = UUID.fromString(result.getString("player_id"));
                    int wins = result.getInt("wins");
                    int sold = result.getInt("sold");
                    int top3 = result.getInt("top_3");

                    stats.put(playerId, new PlayerStats(playerId, wins, sold, top3));
                }
                stats.putAll(playerStatsRegistry.getPlayerStatsMap()); // Some data may not be in the database yet, so we need to add it here
            } catch (SQLException e) {
                System.err.println("Error while fetching event stats from database: " + e.getMessage());
            }

            return stats;
        });
    }
}
