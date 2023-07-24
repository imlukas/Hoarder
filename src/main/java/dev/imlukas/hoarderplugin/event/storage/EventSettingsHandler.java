package dev.imlukas.hoarderplugin.event.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.item.HoarderItem;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSettingsHandler extends YMLBase {

    private final Map<String, EventSettings> eventSettingsMap = new HashMap<>();
    public EventSettingsHandler(HoarderPlugin plugin) {
        super(plugin, "settings.yml");
        load();
    }

    public void load() {
        FileConfiguration config = getConfiguration();
        boolean randomMaterial = config.getBoolean("random-material");

        List<HoarderItem> hoarderItemList = new ArrayList<>();
        for (String key : config.getConfigurationSection("whitelisted-materials").getKeys(false)) {
            Material material = Material.getMaterial(key);
            double value = config.getDouble("whitelisted-materials." + key);
            hoarderItemList.add(new HoarderItem(material, value));
        }

        HoarderEventSettings eventSettings = new HoarderEventSettings(randomMaterial, hoarderItemList);

        if (!randomMaterial) {
            HoarderItem fixedItem = new HoarderItem(Material.getMaterial(config.getString("fixed-material.material")),
                    config.getDouble("fixed-material.value"));

            eventSettings.setFixedItem(fixedItem);
        }

        eventSettingsMap.put(eventSettings.getEventIdentifier(), eventSettings);
    }

    public Map<String, EventSettings> getEventSettingsMap() {
        return eventSettingsMap;
    }

    public EventSettings getEventSettings(String eventIdentifier) {
        return eventSettingsMap.get(eventIdentifier);
    }
}
