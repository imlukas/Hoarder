package dev.imlukas.hoarderplugin.prize.registry;

import com.google.common.collect.Sets;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.utils.collection.ListUtils;
import lombok.Getter;

import java.util.*;

@Getter
public class PrizeRegistry {


    private final LinkedList<EventPrize> prizes = new LinkedList<>();

    public void registerPrize(EventPrize prize) {
        prizes.add(prize);
    }

    public void unregisterPrize(EventPrize prize) {
        prizes.remove(prize);
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
        return ListUtils.getRandom(prizes);
    }

}
