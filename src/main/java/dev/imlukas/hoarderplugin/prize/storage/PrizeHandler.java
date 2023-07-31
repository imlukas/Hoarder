package dev.imlukas.hoarderplugin.prize.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.item.ItemBuilder;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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
            ItemStack displayItem = ItemBuilder.fromSection(getConfiguration().getConfigurationSection(key + ".display-item"));
            List<String> actions = getConfiguration().getStringList(key + ".actions");

            LinkedList<PrizeAction> parsedActions = new LinkedList<>();
            for (String action : actions) {

                PrizeAction prizeAction = actionRegistry.getAction(action);

                if (prizeAction == null) {
                    continue;
                }

                parsedActions.add(prizeAction);
            }

            prizeRegistry.registerPrize(new EventPrize(key, displayName, displayItem, parsedActions));
        }

        System.out.println("Loaded " + prizeRegistry.getPrizes().size() + " prize(s).");
    }

    public void updatePrize(EventPrize eventPrize) {
        String identifier = eventPrize.getIdentifier();
        ConfigurationSection section = getConfiguration().getConfigurationSection(identifier);
        setValues(eventPrize, section);

        save();
    }

    public void createPrize(EventPrize eventPrize) {
        String identifier = eventPrize.getIdentifier();
        ConfigurationSection section = getConfiguration().createSection(identifier);
        setValues(eventPrize, section);

        save();
    }

    public void removePrize(EventPrize eventPrize) {
        String identifier = eventPrize.getIdentifier();
        getConfiguration().set(identifier, null);

        save();
    }

    public void setValues(EventPrize prize, ConfigurationSection section) {
        section.set("display-name", prize.getDisplayName());
        ItemBuilder.toSection(prize.getDisplayItem(), getConfiguration(), prize.getIdentifier() + ".display-item");

        List<String> actions = new LinkedList<>();

        for (PrizeAction action : prize.getActions()) {
            actions.add(action.getFullInput());
        }

        section.set("actions", actions);
    }
}
