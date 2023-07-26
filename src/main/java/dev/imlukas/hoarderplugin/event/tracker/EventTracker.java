package dev.imlukas.hoarderplugin.event.tracker;

import dev.imlukas.hoarderplugin.event.impl.Event;

public class EventTracker {

    private Event activeEvent;
    private Event lastEvent;

    public void setActiveEvent(Event event) {
        activeEvent = event;
    }
    public void setLastEvent(Event lastEvent) {
        this.activeEvent = null;
        this.lastEvent = lastEvent;
    }
    public Event getActiveEvent() {
        return activeEvent;
    }

    public Event getLastEvent() {
        return lastEvent;
    }
}
