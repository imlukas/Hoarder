package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.HoarderEventData;
import dev.imlukas.hoarderplugin.event.phase.impl.EndPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.HoarderEventPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.PreStartPhase;
import dev.imlukas.hoarderplugin.utils.time.Time;

import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event{

    private final HoarderEventData eventData;

    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);
        eventData = new HoarderEventData();

        addPhase(new PreStartPhase(this, () -> {
            plugin.getServer().broadcastMessage("The Hoarder Event is Starting");
        }, new Time(15, TimeUnit.SECONDS)));

        addPhase(new HoarderEventPhase(this, new Time(10, TimeUnit.MINUTES)));
        addPhase(new EndPhase(this, new Time(5, TimeUnit.SECONDS)));
    }

    @Override
    public String getIdentifier() {
        return "hoarder";
    }
}
