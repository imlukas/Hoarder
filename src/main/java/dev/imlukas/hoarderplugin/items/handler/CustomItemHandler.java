package dev.imlukas.hoarderplugin.items.handler;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.items.CustomItem;
import dev.imlukas.hoarderplugin.items.registry.CustomItemRegistry;
import dev.imlukas.hoarderplugin.utils.item.ItemBuilder;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CustomItemHandler extends YMLBase {

    private final CustomItemRegistry customItemRegistry;
    public CustomItemHandler(HoarderPlugin plugin) {
        super(plugin, "items.yml");
        customItemRegistry = plugin.getCustomItemRegistry();

        load();
    }

    public void load() {

        for (String key : getConfiguration().getKeys(false)) {
            ConfigurationSection itemSection = getConfiguration().getConfigurationSection(key);
            ItemStack itemStack = ItemBuilder.fromSection(itemSection);
            CustomItem customItem = new CustomItem(key, itemStack);
            customItemRegistry.register(customItem);
        }

        System.out.println("Loaded " + customItemRegistry.getCustomItems().size() + " custom items.");
    }
}
