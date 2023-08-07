package dev.imlukas.hoarderplugin.event.registry;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * NOT USED. This class is responsible for registering events in case I want to add more events in the future.
 */
public class EventRegistry {

    private final HoarderPlugin plugin;
    private final Map<String, Function<HoarderPlugin, Event>> registeredEvents = new HashMap<>();


    public EventRegistry(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvent(String identifier, Function<HoarderPlugin, Event> event) {
        registeredEvents.put(identifier, event);
    }

    public Event getEvent(String identifier) {
        return registeredEvents.get(identifier).apply(plugin);
    }

    public List<String> getRegisteredEvents() {
        return new ArrayList<>(registeredEvents.keySet());
    }

}
