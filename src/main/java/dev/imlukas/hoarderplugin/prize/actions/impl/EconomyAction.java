package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class EconomyAction implements PrizeAction {

    private final String input;
    private final Economy economy;
    public EconomyAction(HoarderPlugin plugin, String input) {
        this.input = input;
        this.economy = plugin.getEconomy();
    }

    @Override
    public void handle(Player player) {
        economy.depositPlayer(player, Double.parseDouble(input));
    }

    @Override
    public String getIdentifier() {
        return "ECONOMY";
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public String getFullInput() {
        return getIdentifier() + ":" + getInput();
    }
}
