package dev.imlukas.hoarderplugin.event.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class EventData<T extends PlayerEventData> {

    protected final List<T> participants = new ArrayList<>();

    public abstract void addParticipant(Player player);

    public void removeParticipant(Player player) {
        participants.removeIf(participant -> participant.getPlayerId().equals(player.getUniqueId()));
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
