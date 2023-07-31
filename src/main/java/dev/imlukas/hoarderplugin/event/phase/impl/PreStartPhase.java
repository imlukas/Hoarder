package dev.imlukas.hoarderplugin.event.phase.impl;

import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.utils.time.Time;

public class PreStartPhase extends EventPhase {

    private final Runnable runnable;

    public PreStartPhase(Event event, Time duration) {
        this(event, () -> event.getPlugin().getServer().broadcastMessage("Event " + event.getIdentifier() + " is starting!"),
                duration);
    }

    public PreStartPhase(Event event, Runnable runnable, Time duration) {
        super(event.getPlugin(), duration);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public void end() {
        if (onEnd != null) {
            onEnd.run();
        }
    }
}
