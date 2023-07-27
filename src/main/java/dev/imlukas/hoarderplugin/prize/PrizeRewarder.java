package dev.imlukas.hoarderplugin.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;

import java.util.Map;
import java.util.function.Function;

public class PrizeRewarder {

    private final PrizeRegistry prizeRegistry;

    public PrizeRewarder(HoarderPlugin plugin) {
        this.prizeRegistry = plugin.getPrizeRegistry();
    }

    public Map<EventPrize, Boolean> getReward(int position) {
        return switch (position) {
            case 1 -> prizeRegistry.getRandomPrizes(3);
            case 2 -> prizeRegistry.getRandomPrizes(2);
            default -> prizeRegistry.getRandomPrizes(1);
        };
    }

    public Map<EventPrize, Boolean> getReward() {
        return getReward(0);
    }
}