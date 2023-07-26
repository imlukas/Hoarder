package dev.imlukas.hoarderplugin.prize;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class EventPrize {

    private final String identifier, displayName;

    private final LinkedList<PrizeAction> actions; // Use linked list to guarantee that actions are run in order

    public EventPrize(String identifier, String displayName, LinkedList<PrizeAction> actions) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.actions = actions;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LinkedList<PrizeAction> getActions() {
        return actions;
    }

    public void runAll(Player player) {
        actions.forEach((action) -> action.handle(player));
    }
}
