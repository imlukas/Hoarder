package dev.imlukas.hoarderplugin.prize.registry;

import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.utils.collection.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public Map<EventPrize, Boolean> getRandomPrizes(int amount) {
        Map<EventPrize, Boolean> randomPrizes = new HashMap<>();

        for (int i = 0; i < amount; i++) {
            EventPrize prize = ListUtils.getRandom(prizes.values());

            if (randomPrizes.containsKey(prize) && prizes.size() >= amount) {
                i--;
                continue;
            }
            System.out.println("Added prize " + prize.getIdentifier() + " to random prizes.");
            randomPrizes.put(prize, false);
        }

        return randomPrizes;
    }

    public EventPrize getRandomPrize() {
        return ListUtils.getRandom(prizes.values());
    }

}
