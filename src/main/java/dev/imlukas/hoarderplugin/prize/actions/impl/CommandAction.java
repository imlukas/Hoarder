package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.entity.Player;

public class CommandAction implements PrizeAction {

    private final String command;

    public CommandAction(String command) {
        this.command = command;
    }

    @Override
    public void handle(Player player) {
        player.performCommand(this.command);
    }

    @Override
    public String getIdentifier() {
        return "COMMAND";
    }

    @Override
    public String getInput() {
        return command;
    }

    @Override
    public String getFullInput() {
        return getIdentifier() + ":" + getInput();
    }
}
