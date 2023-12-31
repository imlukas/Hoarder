package dev.imlukas.hoarderplugin.event.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class EventData<T extends PlayerEventData> {

    protected final List<T> participants = new ArrayList<>();

    public abstract T addParticipant(Player player);

    public void removeParticipant(Player player) {
        participants.removeIf(participant -> participant.getPlayerId().equals(player.getUniqueId()));
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
