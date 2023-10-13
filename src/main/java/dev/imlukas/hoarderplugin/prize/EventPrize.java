package dev.imlukas.hoarderplugin.prize;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class EventPrize {
    private final UUID prizeId;

    private final String displayName;
    private ItemStack displayItem;
    private boolean claimed;

    public EventPrize(String displayName, ItemStack displayItem) {
        this(UUID.randomUUID(), displayName, displayItem, false);
    }

    public EventPrize(UUID prizeId, String displayName, ItemStack displayItem, boolean claimed) {
        this.prizeId = prizeId;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.claimed = claimed;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public UUID getPrizeId() {
        return prizeId;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public EventPrize copy() {
        return new EventPrize(prizeId, displayName, displayItem, false);
    }

    public void give(Player player) {
        player.getInventory().addItem(displayItem.clone());
    }
}
