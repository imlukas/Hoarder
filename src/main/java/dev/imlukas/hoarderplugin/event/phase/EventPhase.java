package dev.imlukas.hoarderplugin.event.phase;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.time.Time;
public abstract class EventPhase {

    protected final HoarderPlugin plugin;
    protected final Time duration;

    public EventPhase(HoarderPlugin plugin, Time duration) {
        this.plugin = plugin;
        this.duration = duration;
    }

    public Time getDuration() {
        return duration;
    }

    public abstract void run();

    public void runAfter(Time time) {
        new ScheduleBuilder(plugin).in(time.asTicks()).ticks().run(this::run).sync().start();
    }
}
