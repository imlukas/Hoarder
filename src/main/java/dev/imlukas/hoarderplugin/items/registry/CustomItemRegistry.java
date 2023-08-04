package dev.imlukas.hoarderplugin.items.registry;

import dev.imlukas.hoarderplugin.items.CustomItem;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomItemRegistry {

    private final Map<String, CustomItem> customItems = new HashMap<>();

    public void register(CustomItem customItem) {
        this.customItems.put(customItem.getIdentifier(), customItem);
    }

    public CustomItem get(String identifier) {
        try {
            return this.customItems.get(identifier);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean contains(String identifier) {
        return this.customItems.containsKey(identifier);
    }
}
