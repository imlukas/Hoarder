package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.EventData;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.event.storage.EventSettings;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;

import java.util.LinkedList;
import java.util.List;

public abstract class Event {

    protected final HoarderPlugin plugin;
    protected final PrizeRegistry prizeRegistry;

    private final LinkedList<EventPhase> phases = new LinkedList<>();

    public Event(HoarderPlugin plugin) {
        this.plugin = plugin;
        this.prizeRegistry = plugin.getPrizeRegistry();
    }

    public HoarderPlugin getPlugin() {
        return plugin;
    }

    public PrizeRegistry getPrizeRegistry() {
        return prizeRegistry;
    }

    public List<EventPhase> getPhases() {
        return phases;
    }

    public abstract EventData<?> getEventData();

    public abstract EventSettings getEventSettings();

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
