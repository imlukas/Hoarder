package dev.imlukas.hoarderplugin.event.data;

import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventData<T extends PlayerEventData> {

    protected final List<T> participants = new ArrayList<>();

    public void addParticipant(Player player) {
        participants.add((T) new PlayerEventData(player.getUniqueId()));
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
