package dev.imlukas.hoarderplugin.event.settings;

import lombok.Getter;

@Getter
public abstract class EventSettings {

    private final String eventIdentifier;

    public EventSettings(String eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

}
