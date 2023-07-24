package dev.imlukas.hoarderplugin.event.data.player;

import java.util.UUID;

public class HoarderPlayerEventData extends PlayerEventData{

    private int soldItems = 0;

    public HoarderPlayerEventData(UUID playerId) {
        super(playerId);
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
}