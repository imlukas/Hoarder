package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.event.data.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.utils.MapUtil;

import java.util.*;

public class HoarderEventData extends EventData<HoarderPlayerEventData> {
    private final HoarderItem activeItem;

    public HoarderEventData(HoarderItem activeItem) {
        this.activeItem = activeItem;
    }

    public HoarderItem getActiveItem() {
        return activeItem;
    }

    public Map<Integer, HoarderPlayerEventData> getTop() {
        // get scores and PlayerEventData and order them by highest to lowest

        Map<Integer, HoarderPlayerEventData> scores = new HashMap<>();

        for (HoarderPlayerEventData participant : participants) {
            scores.put(participant.getSoldItems(), participant);
        }


        Map<Integer, HoarderPlayerEventData> sortedScores = new TreeMap<>(Comparator.reverseOrder());
        sortedScores.putAll(scores);

        Map<Integer, HoarderPlayerEventData> top = new HashMap<>();

        int i = 0;
        for (Map.Entry<Integer, HoarderPlayerEventData> integerHoarderPlayerEventDataEntry : scores.entrySet()) {
            top.put(i, integerHoarderPlayerEventDataEntry.getValue());
        }

        for (Map.Entry<Integer, HoarderPlayerEventData> integerHoarderPlayerEventDataEntry : top.entrySet()) {
            System.out.println(integerHoarderPlayerEventDataEntry.getValue().getPlayer().getName() + " # " + integerHoarderPlayerEventDataEntry.getKey());
        }
        return top;
    }

}
