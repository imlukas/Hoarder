package dev.imlukas.hoarderplugin.event.phase.impl;

import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.utils.time.Time;

public class HoarderEventPhase extends EventPhase {

    private final HoarderEvent event;

    public HoarderEventPhase(HoarderEvent event, Time duration) {
        super(event.getPlugin(), duration);
        this.event = event;
    }

    @Override
    public void run() {

    }
}
