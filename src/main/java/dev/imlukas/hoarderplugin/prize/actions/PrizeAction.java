package dev.imlukas.hoarderplugin.prize.actions;

import org.bukkit.entity.Player;

public interface PrizeAction {

    void handle(Player player);

    String getIdentifier();

    String getInput();

    String getFullInput();
}
