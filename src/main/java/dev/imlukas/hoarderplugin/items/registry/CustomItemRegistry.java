package dev.imlukas.hoarderplugin.items.registry;

import dev.imlukas.hoarderplugin.items.CustomItem;

import java.util.HashMap;
import java.util.Map;

public class CustomItemRegistry {

    private final Map<String, CustomItem> customItems = new HashMap<>();

    public void register(CustomItem customItem) {
        this.customItems.put(customItem.getIdentifier(), customItem);
    }

    public CustomItem get(String identifier) {
        return this.customItems.get(identifier);
    }

    public Map<String, CustomItem> getCustomItems() {
        return customItems;
    }

    public boolean contains(String identifier) {
        return this.customItems.containsKey(identifier);
    }
}
