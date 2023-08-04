package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.prize.EventPrize;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerEventData {

    private final UUID playerId;
    private final LinkedList<EventPrize> availablePrizes = new LinkedList<>();

    public PlayerEventData(UUID playerId) {
        this.playerId = playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public void addPrizes(List<EventPrize> prizes) {
        availablePrizes.addAll(prizes);
    }

    public void addPrize(EventPrize prize) {
        availablePrizes.add(prize);
    }
}
