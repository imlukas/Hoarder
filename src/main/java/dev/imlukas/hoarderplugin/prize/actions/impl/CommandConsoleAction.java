package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandConsoleAction implements PrizeAction {

    private final String command;

    public CommandConsoleAction(String command) {
        this.command = command;
    }

    @Override
    public void handle(Player player) {
        Bukkit.getConsoleSender().sendMessage(command);
    }
}
