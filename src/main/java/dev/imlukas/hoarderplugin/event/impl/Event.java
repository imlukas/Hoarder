package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;

import java.util.LinkedList;
import java.util.List;

public abstract class Event {

    protected final HoarderPlugin plugin;
    private final LinkedList<EventPhase> phases = new LinkedList<>();

    public Event(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    public HoarderPlugin getPlugin() {
        return plugin;
    }

    public List<EventPhase> getPhases() {
        return phases;
    }

    public abstract String getIdentifier();

    public void addPhase(EventPhase phase) {
        phases.add(phase);
    }

    public void start() {
        EventPhase firstPhase = phases.getFirst();

        for (int i = 1; i < phases.size(); i++) {
            phases.get(i).runAfter(phases.get(i - 1).getDuration());
        }

        firstPhase.run();
    }
}
