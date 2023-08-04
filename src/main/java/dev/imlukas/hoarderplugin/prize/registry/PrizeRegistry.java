package dev.imlukas.hoarderplugin.prize.registry;

import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.utils.collection.ListUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PrizeRegistry {


    private final Map<String, EventPrize> prizes = new HashMap<>();

    public void registerPrize(EventPrize prize) {
        prizes.put(prize.getIdentifier(), prize);
    }

    public void unregisterPrize(EventPrize prize) {
        prizes.remove(prize.getIdentifier());
    }

    public EventPrize getPrize(String identifier) {
        return prizes.get(identifier);
    }

    public List<EventPrize> getRandomPrizes(int amount) {
        List<EventPrize> randomPrizes = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            EventPrize randomPrize = getRandomPrize();

            if (randomPrize == null) {
                continue;
            }

            if (randomPrizes.contains(randomPrize)) {
                i--;
                continue;
            }

            randomPrizes.add(randomPrize.copy());
        }

        return randomPrizes;
    }

    public EventPrize getRandomPrize() {
        return ListUtils.getRandom(prizes.values());
    }

    public List<String> getPrizeNames() {
        return new ArrayList<>(prizes.keySet());
    }

}
