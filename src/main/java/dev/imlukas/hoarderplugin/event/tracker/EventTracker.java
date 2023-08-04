package dev.imlukas.hoarderplugin.event.tracker;

import dev.imlukas.hoarderplugin.event.Event;
import lombok.Getter;

/**
 * Tracks the active event and the last event
 */
@Getter
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

}
