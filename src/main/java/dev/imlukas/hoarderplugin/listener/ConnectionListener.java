package dev.imlukas.hoarderplugin.listener;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final PlayerStatsRegistry playerStatsRegistry;
    private final SQLDatabase sqlDatabase;
    private final EventTracker tracker;

    public ConnectionListener(HoarderPlugin plugin) {
        this.sqlDatabase = plugin.getSqlDatabase();
        this.playerStatsRegistry = plugin.getPlayerStatsRegistry();
        this.tracker = plugin.getEventTracker();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        sqlDatabase.getOrCreateTable("hoarder_stats")
                .executeQuery("SELECT * FROM hoarder_stats WHERE player_id = '" + playerId + "'")
                .thenAccept(result -> {
                    try {
                        if (result.next()) {
                            playerStatsRegistry.register(new PlayerStats(playerId, result.getInt("wins"), result.getInt("sold"), result.getInt("top_3")));
                        } else {
                            playerStatsRegistry.register(new PlayerStats(playerId, 0, 0, 0));
                        }

                    } catch (Exception e) {
                        System.err.println("Error while loading player stats from database: " + e.getMessage());
                    }
                });

        Event activeEvent = tracker.getActiveEvent();

        if (activeEvent == null) {
            return;
        }

        activeEvent.getEventData().addParticipant(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerStats playerStats = playerStatsRegistry.getPlayerStats(playerId);

        if (playerStats.getSoldItems() == 0) {
            return;
        }

        Map<String, Object> values = Map.of("player_id", playerId.toString(), "wins", playerStats.getWins(), "sold", playerStats.getSoldItems(), "top_3", playerStats.getTop3());
        sqlDatabase.getOrCreateTable("hoarder_stats")
                .insertOnDuplicate(values, "wins = VALUES(wins), sold = VALUES(sold), top_3 = VALUES(top_3)")
                .thenRun(() -> playerStatsRegistry.unregister(playerId));
    }
}
