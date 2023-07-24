package dev.imlukas.hoarderplugin.event.data.player;

import dev.imlukas.hoarderplugin.prize.EventPrize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerEventData {

    private final UUID playerId;
    private final List<EventPrize> availablePrizes = new ArrayList<>();


    public PlayerEventData(UUID playerId) {
        this.playerId = playerId;
    }


    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public List<EventPrize> getAvailablePrizes() {
        return availablePrizes;
    }

    public void addAvailablePrize(EventPrize prize) {
        availablePrizes.add(prize);
    }

    public void removeAvailablePrize(EventPrize prize) {
        availablePrizes.remove(prize);
    }
}
