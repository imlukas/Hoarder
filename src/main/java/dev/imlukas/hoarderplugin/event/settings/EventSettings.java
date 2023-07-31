package dev.imlukas.hoarderplugin.event.settings;

public class EventSettings {

    private final String eventIdentifier;

    public EventSettings(String eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public String getEventIdentifier() {
        return eventIdentifier;
    }
}
