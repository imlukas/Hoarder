package dev.imlukas.hoarderplugin.event.settings;

import java.util.Map;

public abstract class EventSettings {

    private final String eventIdentifier;

    public EventSettings(String eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public String getEventIdentifier() {
        return eventIdentifier;
    }
}
