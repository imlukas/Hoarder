package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventData<T extends PlayerEventData> {

    protected final List<T> participants = new ArrayList<>();

    public void addParticipant(PlayerEventData participant) {
        participants.add((T) participant);
    }

    public void removeParticipant(UUID playerId) {
        participants.removeIf(participant -> participant.getPlayerId().equals(playerId));
    }

    public List<T> getParticipants() {
        return participants;
    }

    public T getPlayerData(UUID participantId) {
        for (T participant : participants) {
            if (participant.getPlayerId().equals(participantId)) {
                return participant;
            }
        }
        return null;
    }
}
