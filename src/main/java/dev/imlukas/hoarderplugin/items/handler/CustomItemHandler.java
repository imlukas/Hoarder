package dev.imlukas.hoarderplugin.items.handler;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;

public class CustomItemHandler extends YMLBase {

    public CustomItemHandler(HoarderPlugin plugin) {
        super(plugin, "items.yml");
    }
}
