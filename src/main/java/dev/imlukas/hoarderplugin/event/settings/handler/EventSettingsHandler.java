package dev.imlukas.hoarderplugin.event.settings.handler;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.hoarder.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.settings.impl.hoarder.HoarderEventSettings;
import dev.imlukas.hoarderplugin.event.settings.registry.EventSettingsRegistry;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class EventSettingsHandler extends YMLBase {

    private final EventSettingsRegistry eventSettingsRegistry;

    public EventSettingsHandler(HoarderPlugin plugin) {
        super(plugin, "settings.yml");
        this.eventSettingsRegistry = plugin.getEventSettingsRegistry();
        load();
    }

    // TODO: make this not a registry type thing, currently this is not dynamic and would be a mess with 2+ events.
    public void load() {
        FileConfiguration config = getConfiguration();
        boolean randomMaterial = config.getBoolean("random-material");

        List<HoarderItem> hoarderItemList = new ArrayList<>();
        for (String key : config.getConfigurationSection("material-whitelist").getKeys(false)) {
            Material material = Material.getMaterial(key);
            double value = config.getDouble("material-whitelist." + key);
            hoarderItemList.add(new HoarderItem(material, value));
        }

        Time startingTime = Time.parseTime(config.getString("starting-time"));
        Time eventTime = Time.parseTime(config.getString("event-time"));

        HoarderEventSettings eventSettings = new HoarderEventSettings(randomMaterial, hoarderItemList, startingTime, eventTime);

        HoarderItem fixedItem = new HoarderItem(Material.getMaterial(config.getString("fixed-material.material")),
                config.getDouble("fixed-material.value"));

        eventSettings.setFixedItem(fixedItem);

        eventSettingsRegistry.register(eventSettings);
        System.out.println("Loaded settings for event " + eventSettings.getEventIdentifier() + ".");
    }

    public void updateSetting(String key, Object value) {
        FileConfiguration config = getConfiguration();
        config.set(key, value);
        save();
    }

    public void updatedFixed(HoarderItem item) {
        FileConfiguration config = getConfiguration();
        config.set("fixed-material", null);
        config.set("fixed-material.material", item.getMaterial().name());
        config.set("fixed-material.value", item.getValue());
        save();
    }

    public void updateWhitelist(List<HoarderItem> items) {
        FileConfiguration config = getConfiguration();
        config.set("material-whitelist", null);
        for (HoarderItem item : items) {
            config.set("material-whitelist." + item.getMaterial().name(), item.getValue());
        }
        save();
    }
}
