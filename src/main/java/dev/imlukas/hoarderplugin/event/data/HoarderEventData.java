package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.event.data.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.utils.MapUtil;

import java.util.HashMap;
import java.util.Map;

public class HoarderEventData extends EventData<HoarderPlayerEventData> {
    private final HoarderItem activeItem;

    public HoarderEventData(HoarderItem activeItem) {
        this.activeItem = activeItem;
    }

    public HoarderItem getActiveItem() {
        return activeItem;
    }

    public Map<HoarderPlayerEventData, Integer> getTop() {
        // get scores and PlayerEventData and order them by highest to lowest

        Map<HoarderPlayerEventData, Integer> scores = new HashMap<>();

        for (HoarderPlayerEventData participant : participants) {

            scores.put(participant, participant.getSoldItems());
        }

        return MapUtil.sortByValue(scores);


    }

}
