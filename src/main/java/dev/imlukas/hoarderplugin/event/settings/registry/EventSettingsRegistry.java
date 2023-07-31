package dev.imlukas.hoarderplugin.event.settings.registry;

import dev.imlukas.hoarderplugin.event.settings.EventSettings;

import java.util.HashMap;
import java.util.Map;

public class EventSettingsRegistry {

    private final Map<String, EventSettings> eventSettingsMap = new HashMap<>();

    public void register(EventSettings eventSettings) {
        eventSettingsMap.put(eventSettings.getEventIdentifier(), eventSettings);
    }

    public void unregister(String eventIdentifier) {
        eventSettingsMap.remove(eventIdentifier);
    }

    public EventSettings get(String eventIdentifier) {
        return eventSettingsMap.get(eventIdentifier);
    }

    public boolean isRegistered(String eventIdentifier) {
        return eventSettingsMap.containsKey(eventIdentifier);
    }



}
