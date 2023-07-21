package dev.imlukas.hoarderplugin.event.data.player;

import java.util.UUID;

public class PlayerEventData {

    private final UUID playerId;
    private int soldItems = 0;

    public PlayerEventData(UUID playerId) {
        this.playerId = playerId;
    }

    public void addSoldItem() {
        soldItems++;
    }

    public void addSoldItem(int amount) {
        soldItems += amount;
    }

    public int getSoldItems() {
        return soldItems;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
