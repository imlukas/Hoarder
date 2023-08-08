package dev.imlukas.hoarderplugin.storage.sql;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;

import java.sql.SQLException;
import java.util.*;
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

    public CompletableFuture<List<UUID>> fetchLastWinners() {
        List<UUID> winners = new ArrayList<>();
        return sqlDatabase.getOrCreateTable("hoarder_winners").executeQuery("SELECT * FROM hoarder_winners ORDER BY id DESC LIMIT 3").thenApply((resultSet -> {
            try {
                while (resultSet.next()) {
                    UUID playerId = UUID.fromString(resultSet.getString("top1"));

                    winners.add(playerId);
                }
            } catch (SQLException e) {
                System.err.println("Error while fetching event stats from database: " + e.getMessage());
            }

            return winners;
        }));
    }

    public CompletableFuture<PlayerStats> fetchPlayerStats(UUID playerId) {
        return sqlDatabase.getOrCreateTable("hoarder_stats")
                .executeQuery("SELECT * FROM hoarder_stats WHERE player_id = '" + playerId + "'")
                .thenApply(result -> {
                    try {
                        if (result.next()) {
                           return new PlayerStats(playerId, result.getInt("wins"), result.getInt("sold"), result.getInt("top_3"));
                        } else {
                            return new PlayerStats(playerId, 0, 0, 0);
                        }

                    } catch (Exception e) {
                        System.err.println("Error while loading player stats from database: " + e.getMessage());
                    }

                    return new PlayerStats(playerId, 0, 0, 0);
                });
    }
}
