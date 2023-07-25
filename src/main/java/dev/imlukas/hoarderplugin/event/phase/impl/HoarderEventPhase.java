package dev.imlukas.hoarderplugin.event.phase.impl;

import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.entity.Player;

import java.util.List;

public class HoarderEventPhase extends EventPhase {

    private final HoarderEvent event;
    private final Messages messages;
    private final EventRegistry eventRegistry;

    public HoarderEventPhase(HoarderEvent event, Time duration) {
        super(event.getPlugin(), duration);
        this.event = event;
        this.messages = event.getPlugin().getMessages();
        this.eventRegistry = event.getPlugin().getEventRegistry();
    }

    @Override
    public void run() {
        eventRegistry.setActiveEvent(event);

        List<Placeholder<Player>> placeholders = List.of(
                new Placeholder<>("duration", duration.toString()),
                new Placeholder<>("item", TextUtils.enumToText(event.getEventData().getActiveItem().getMaterial())),
                new Placeholder<>("participants", String.valueOf(event.getEventData().getParticipants().size()))
        );

        for (PlayerEventData playerEventData : event.getEventData().getParticipants()) {
            messages.sendMessage(playerEventData.getPlayer(), "hoarder.start", placeholders);
        }
    }
}
