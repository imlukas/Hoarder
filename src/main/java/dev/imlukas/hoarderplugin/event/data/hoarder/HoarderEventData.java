package dev.imlukas.hoarderplugin.event.data.hoarder;

import dev.imlukas.hoarderplugin.event.data.EventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.item.HoarderItem;
import dev.imlukas.hoarderplugin.utils.collection.MapUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class HoarderEventData extends EventData<HoarderPlayerEventData> {
    private final HoarderItem activeItem;

    public HoarderEventData(HoarderItem activeItem) {
        this.activeItem = activeItem;
    }

    public Map<Integer, HoarderPlayerEventData> getLeaderboard() {
        Map<HoarderPlayerEventData, Integer> scores = new HashMap<>();

        for (HoarderPlayerEventData participant : participants) {
            scores.put(participant, participant.getSoldItems());
        }

        return MapUtils.getLeaderboardMap(scores);
    }

    public boolean isTop3(UUID player) {
        Map<Integer, HoarderPlayerEventData> top = getLeaderboard();

        for (int i = 1; i < 4; i++) {
            if (top.get(i).getPlayerId().equals(player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public HoarderPlayerEventData addParticipant(Player player) {
        for (HoarderPlayerEventData participant : participants) {
            if (participant.getPlayerId().equals(player.getUniqueId())) {
                return participant;
            }
        }
        HoarderPlayerEventData participant = new HoarderPlayerEventData(player.getUniqueId());

        participants.add(participant);
        return participant;
    }
}
