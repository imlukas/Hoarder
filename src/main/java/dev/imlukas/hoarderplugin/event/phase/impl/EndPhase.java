package dev.imlukas.hoarderplugin.event.phase.impl;

import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.utils.time.Time;

public class EndPhase extends EventPhase {

    private final Runnable runnable;
    public EndPhase(Event event, Time duration) {
        this(event, () -> event.getPlugin().getServer().broadcastMessage("Event " + event.getIdentifier() + " is ending!"),
                duration);
    }

    public EndPhase(Event event, Runnable runnable, Time duration) {
        super(event.getPlugin(), duration);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
