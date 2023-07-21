package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;

import java.util.*;

public class HoarderEventData {

    private final List<PlayerEventData> participants = new ArrayList<>();

    public List<PlayerEventData> getParticipants() {
        return participants;
    }

    public PlayerEventData getParticipant(UUID participantId) {
        for (PlayerEventData participant : participants) {
            if (participant.getPlayerId().equals(participantId)) {
                return participant;
            }
        }
        return null;
    }


    public Map<Integer, PlayerEventData> getTop10() {
        Map<PlayerEventData, Integer> scores = new HashMap<>();

        for (PlayerEventData participant : participants) {
            scores.put(participant, participant.getSoldItems());
        }

        scores.entrySet().stream().sorted(Map.Entry.comparingByValue());

        Map<Integer, PlayerEventData> top10 = new HashMap<>();

        int i = 0;

        for (Map.Entry<PlayerEventData, Integer> entry : scores.entrySet()) {
            if (i == 10) {
                break;
            }
            top10.put(i, entry.getKey());
            i++;
        }

        return top10;
    }

}
