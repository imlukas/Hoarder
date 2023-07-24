package dev.imlukas.hoarderplugin.prize.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class PrizeHandler extends YMLBase {
    private final PrizeRegistry prizeRegistry;
    private final ActionRegistry actionRegistry;

    public PrizeHandler(HoarderPlugin plugin) {
        super(plugin, "prizes.yml");
        prizeRegistry = plugin.getPrizeRegistry();
        actionRegistry = plugin.getActionRegistry();

        load();
    }

    public void load() {
        for (String key : getConfiguration().getKeys(false)) {
            List<String> actions = getConfiguration().getStringList(key);

            LinkedList<PrizeAction> parsedActions = new LinkedList<>();
            for (String action : actions) {
                parsedActions.add(actionRegistry.getAction(action));
            }

            prizeRegistry.registerPrize(new EventPrize(key, parsedActions));
        }
    }
}
