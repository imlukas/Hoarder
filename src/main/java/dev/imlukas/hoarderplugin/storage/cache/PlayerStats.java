package dev.imlukas.hoarderplugin.storage.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerStats {

    private final UUID playerId;
    private int wins, soldItems, top3;

    public PlayerStats(Map<String, Object> map) {
        this.playerId = UUID.fromString((String) map.get("player_id"));
        this.wins = (int) map.get("wins");
        this.soldItems = (int) map.get("sold_items");
        this.top3 = (int) map.get("top_3");
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerId);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public void addWin() {
        wins++;
    }

    public void addSoldItems(int amount) {
        soldItems += amount;
    }

    public void addTop3() {
        top3++;
    }
}
