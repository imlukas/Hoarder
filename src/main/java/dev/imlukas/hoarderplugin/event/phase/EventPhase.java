package dev.imlukas.hoarderplugin.event.phase;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.schedulerutil.ScheduledTask;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.time.Time;

public abstract class EventPhase {

    protected final HoarderPlugin plugin;
    protected final Time duration;
    protected Runnable onEnd;
    protected ScheduledTask runAfterTask;

    public EventPhase(HoarderPlugin plugin, Time duration) {
        this.plugin = plugin;
        this.duration = duration;
    }

    public Time getDuration() {
        return duration;
    }

    public abstract void run();

    public void end() {
        if (onEnd != null) {
            onEnd.run();
        }

        if (runAfterTask != null) {
            runAfterTask.cancel();
        }
    }

    public EventPhase onEnd(Runnable onEnd) {
        this.onEnd = onEnd;
        return this;
    }

    public void runAfter(EventPhase eventPhase, Runnable toRun) {
        runAfterTask = new ScheduleBuilder(plugin).in(eventPhase.getDuration().asTicks()).ticks().run(toRun).sync().start();
    }


}
