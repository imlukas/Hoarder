package dev.imlukas.hoarderplugin.event.data.hoarder;

import dev.imlukas.hoarderplugin.event.data.EventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import org.bukkit.entity.Player;

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

        int i = 1;
        for (Map.Entry<Integer, HoarderPlayerEventData> integerHoarderPlayerEventDataEntry : sortedScores.entrySet()) {
            top.put(i, integerHoarderPlayerEventDataEntry.getValue());
            i++;
        }
        return top;
    }

    @Override
    public void addParticipant(Player player) {
        participants.add(new HoarderPlayerEventData(player.getUniqueId()));
    }
}
