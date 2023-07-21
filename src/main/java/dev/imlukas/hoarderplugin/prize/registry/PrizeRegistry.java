package dev.imlukas.hoarderplugin.prize.registry;

import dev.imlukas.hoarderplugin.prize.EventPrize;

import java.util.HashMap;
import java.util.Map;

public class PrizeRegistry {

    private final Map<String, EventPrize> prizes = new HashMap<>();

    public void registerPrize(EventPrize prize) {
        prizes.put(prize.getIdentifier(), prize);
    }

    public EventPrize getPrize(String identifier) {
        return prizes.get(identifier);
    }

    public Map<String, EventPrize> getPrizes() {
        return prizes;
    }

}
