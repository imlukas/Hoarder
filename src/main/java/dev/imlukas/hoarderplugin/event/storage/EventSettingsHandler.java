package dev.imlukas.hoarderplugin.event.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;

public class EventSettingsHandler extends YMLBase {
    public EventSettingsHandler(HoarderPlugin plugin) {
        super(plugin, "settings.yml");

    }
}
