package dev.imlukas.hoarderplugin.prize.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
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
            String displayName = TextUtils.color(getConfiguration().getString(key + ".display-name"));
            List<String> actions = getConfiguration().getStringList(key + ".actions");

            LinkedList<PrizeAction> parsedActions = new LinkedList<>();
            for (String action : actions) {

                PrizeAction prizeAction = actionRegistry.getAction(action);

                if (prizeAction == null) {
                    continue;
                }

                parsedActions.add(prizeAction);
            }

            prizeRegistry.registerPrize(new EventPrize(key, displayName, parsedActions));
        }

        System.out.println("Loaded " + prizeRegistry.getPrizes().size() + " prizes.");
    }
}
