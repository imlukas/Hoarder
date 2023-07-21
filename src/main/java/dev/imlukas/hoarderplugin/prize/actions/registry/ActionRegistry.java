package dev.imlukas.hoarderplugin.prize.actions.registry;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.impl.*;

import java.util.Map;
import java.util.function.Function;

public class ActionRegistry {

    private static final Map<String, Function<String, PrizeAction>> ACTION_MAP = Map.of(
            "MESSAGE", MessageAction::new,
            "COMMAND", CommandAction::new,
            "GIVE", GiveAction::new,
            "GIVE_CUSTOM", GiveCustomAction::new,
            "ECONOMY", EconomyAction::new);


    public static PrizeAction getAction(String action) {

        Function<String, PrizeAction> function = ACTION_MAP.get(action.substring(0, action.indexOf(":")));

        if (function == null) {
            throw new IllegalArgumentException("Unknown action: " + action);
        }

        return function.apply(action.substring(action.indexOf(":") + 1));
    }
}
