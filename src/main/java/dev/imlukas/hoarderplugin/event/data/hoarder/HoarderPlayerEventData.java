package dev.imlukas.hoarderplugin.event.data.hoarder;

import dev.imlukas.hoarderplugin.event.data.PlayerEventData;
import lombok.Getter;

import java.util.UUID;

@Getter
public class HoarderPlayerEventData extends PlayerEventData {

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

    public void getPosition() {

    }

}
