package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.entity.Player;

public class EconomyAction implements PrizeAction {

    private final String input;

    public EconomyAction(HoarderPlugin plugin, String input) {
        this.input = input;
    }

    @Override
    public void handle(Player player) {

    }
}