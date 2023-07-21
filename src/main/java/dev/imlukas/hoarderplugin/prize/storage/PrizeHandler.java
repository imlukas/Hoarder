package dev.imlukas.hoarderplugin.prize.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

public class PrizeHandler extends YMLBase {
    private final PrizeRegistry prizeRegistry;
    public PrizeHandler(HoarderPlugin plugin) {
        super(plugin, "prizes.yml");
        prizeRegistry = plugin.getPrizeRegistry();
    }
}
