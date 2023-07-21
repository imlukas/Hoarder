package dev.imlukas.hoarderplugin.prize;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class EventPrize {

    private final String identifier;
    private final LinkedList<PrizeAction> actions;

    public EventPrize(String identifier, LinkedList<PrizeAction> actions) {
        this.identifier = identifier;
        this.actions = actions;
    }

    public String getIdentifier() {
        return identifier;
    }

    public LinkedList<PrizeAction> getActions() {
        return actions;
    }

    public void runAll(Player player) {
        actions.forEach((action) -> action.handle(player));
    }
}
