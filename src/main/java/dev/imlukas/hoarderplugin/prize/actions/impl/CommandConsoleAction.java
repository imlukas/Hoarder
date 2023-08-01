package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandConsoleAction implements PrizeAction {

    private String input;

    public CommandConsoleAction(String command) {
        this.input = command;
    }

    @Override
    public void handle(Player player) {
        Bukkit.getConsoleSender().sendMessage(input.replace("%player%", player.getName()));
    }

    @Override
    public String getIdentifier() {
        return "COMMAND_CONSOLE";
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public String getFullInput() {
        return getIdentifier() + ":" + getInput();
    }

    @Override
    public void setInput(String input) {
        this.input = input;
    }
}
