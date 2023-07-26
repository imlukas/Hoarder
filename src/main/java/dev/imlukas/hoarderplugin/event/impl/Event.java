package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.event.data.EventData;
import dev.imlukas.hoarderplugin.event.phase.EventPhase;
import dev.imlukas.hoarderplugin.event.storage.EventSettings;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;

import java.util.LinkedList;
import java.util.List;

public abstract class Event {

    protected final HoarderPlugin plugin;
    protected final PrizeRegistry prizeRegistry;
    protected final EventTracker eventTracker;

    private final LinkedList<EventPhase> phases = new LinkedList<>();
    private EventPhase currentPhase;

    public Event(HoarderPlugin plugin) {
        this.plugin = plugin;
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.eventTracker = plugin.getEventTracker();
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

            EventPhase toRun = phases.get(i);
            EventPhase runAfter = phases.get(i - 1);

            toRun.runAfter(runAfter, () -> {
                if (currentPhase != null) {
                    currentPhase.end();
                }

                currentPhase = toRun;
                currentPhase.run();
            });
        }

        currentPhase = firstPhase;
        firstPhase.run();
        plugin.setupScheduler();
    }

    public void forceEnd() {
        end();
        phases.forEach(EventPhase::end);
    }

    public void end() {
        eventTracker.setLastEvent(this);
    }
}
