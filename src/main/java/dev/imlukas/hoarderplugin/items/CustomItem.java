package dev.imlukas.hoarderplugin.items;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItem {

    @Getter
    private final String identifier;
    private final ItemStack itemStack;

    public CustomItem(String identifier, ItemStack itemStack) {
        this.identifier = identifier;
        this.itemStack = itemStack;
    }

    public void giveItem(Player player) {
        player.getInventory().addItem(this.itemStack.clone());
    }

    public ItemStack getItemStack() {
        return getItemStack(true);
    }

    public ItemStack getItemStack(boolean clone) {
        return clone ? this.itemStack.clone() : this.itemStack;
    }

}
