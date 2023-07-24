package dev.imlukas.hoarderplugin.prize.actions.registry;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.impl.CommandAction;
import dev.imlukas.hoarderplugin.prize.actions.impl.EconomyAction;
import dev.imlukas.hoarderplugin.prize.actions.impl.GiveAction;
import dev.imlukas.hoarderplugin.prize.actions.impl.MessageAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ActionRegistry {

    private static final Map<String, BiFunction<HoarderPlugin, String, PrizeAction>> ACTION_MAP = Map.of(
            "MESSAGE", MessageAction::new,
            "GIVE", GiveAction::new,
            "ECONOMY", EconomyAction::new,
            "COMMAND", (ignored, input) -> new CommandAction(input));

    private final HoarderPlugin plugin;

    public ActionRegistry(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    public PrizeAction getAction(String action) {

        BiFunction<HoarderPlugin, String, PrizeAction> function = ACTION_MAP.get(action.substring(0, action.indexOf(":")));

        if (function == null) {
            throw new IllegalArgumentException("Unknown action: " + action);
        }

        return function.apply(plugin, action.substring(action.indexOf(":") + 1));
    }

    public List<PrizeAction> getActions(List<String> actionsStrings) {
        List<PrizeAction> actions = new ArrayList<>();
        actionsStrings.forEach(action -> actions.add(getAction(action)));
        return actions;
    }
}
