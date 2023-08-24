package dev.imlukas.hoarderplugin.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;

import java.util.List;
import java.util.Map;

public class PrizeRewarder {

    private final PrizeRegistry prizeRegistry;

    public PrizeRewarder(HoarderPlugin plugin) {
        this.prizeRegistry = plugin.getPrizeRegistry();
    }

    public List<EventPrize> getReward(int position) {
        return switch (position) {
            case 1-> prizeRegistry.getRandomPrizes(3);
            case 2 -> prizeRegistry.getRandomPrizes(2);
            default -> prizeRegistry.getRandomPrizes(1);
        };
    }

    public List<EventPrize> getReward() {
        return getReward(0);
    }
}
