package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.items.registry.CustomItemRegistry;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveAction implements PrizeAction {

    private final String input;
    private final CustomItemRegistry customItemRegistry;

    public GiveAction(HoarderPlugin plugin, String input) {
        this.input = input;
        this.customItemRegistry = plugin.getCustomItemRegistry();
    }

    @Override
    public void handle(Player player) {
        String[] split = input.split(" ");
        ItemStack item = customItemRegistry.get(split[0]).getItemStack();

        if (item == null) {
            item = new ItemStack(Material.valueOf(split[0].toUpperCase()));
        }

        if (split.length == 2) {
            item.setAmount(Integer.parseInt(split[1]));
        }

        player.getInventory().addItem(item);
    }
}
