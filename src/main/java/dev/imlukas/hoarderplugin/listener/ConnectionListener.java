package dev.imlukas.hoarderplugin.listener;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
import dev.imlukas.hoarderplugin.storage.sql.SQLHandler;
import dev.imlukas.hoarderplugin.storage.sql.SQLTableType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final PlayerStatsRegistry playerStatsRegistry;
    private final SQLHandler sqlHandler;
    private final SQLDatabase sqlDatabase;

    public ConnectionListener(HoarderPlugin plugin) {
        this.sqlHandler = plugin.getSqlHandler();
        this.sqlDatabase = plugin.getSqlDatabase();
        this.playerStatsRegistry = plugin.getPlayerStatsRegistry();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        sqlHandler.fetchPlayerStats(playerId).thenAccept(playerStatsRegistry::register);
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
        sqlDatabase.getOrCreateTable(SQLTableType.HOARDER_STATS.getName())
                .insertOnDuplicate(values, "wins = VALUES(wins), sold = VALUES(sold), top_3 = VALUES(top_3)")
                .thenRun(() -> playerStatsRegistry.unregister(playerId));
    }
}
