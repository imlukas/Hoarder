package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.prize.EventPrize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerEventData {

    private final UUID playerId;
    private final Map<EventPrize, Boolean> availablePrizes = new HashMap<>();

    public PlayerEventData(UUID playerId) {
        this.playerId = playerId;
    }


    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public Map<EventPrize, Boolean> getAvailablePrizes() {
        return availablePrizes;
    }

    public void setAvailablePrize(EventPrize prize, boolean available) {
        availablePrizes.put(prize, available);
    }

    public void addAvailablePrize(EventPrize prize) {
        availablePrizes.put(prize, false);
    }

    public void addAvailablePrize(Map<EventPrize, Boolean> prizes) {
        availablePrizes.putAll(prizes);
    }


    public void removeAvailablePrize(EventPrize prize) {
        availablePrizes.remove(prize);
    }
}
