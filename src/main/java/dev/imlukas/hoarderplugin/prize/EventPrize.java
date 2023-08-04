package dev.imlukas.hoarderplugin.prize;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

@Getter
public class EventPrize {

    private String identifier, displayName;
    private ItemStack displayItem;

    private LinkedList<PrizeAction> actions; // Use linked list to guarantee that actions are run in order
    private boolean claimed = false;

    public EventPrize(String identifier, String displayName, ItemStack displayItem, LinkedList<PrizeAction> actions) {
        this(identifier, displayName, displayItem, actions, false);
    }

    public EventPrize(String identifier, String displayName, ItemStack displayItem, LinkedList<PrizeAction> actions, boolean claimed) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.actions = actions;
        this.claimed = claimed;
    }

    public EventPrize setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EventPrize setActions(LinkedList<PrizeAction> actions) {
        this.actions = actions;
        return this;
    }

    public EventPrize setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        return this;
    }

    public EventPrize setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public void runAll(Player player) {
        actions.forEach((action) -> action.handle(player));
    }

    public EventPrize copy() {
        return new EventPrize(identifier, displayName, displayItem, actions);
    }
}
