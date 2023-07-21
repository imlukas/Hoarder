package dev.imlukas.hoarderplugin.utils.schedulerutil.builders;

import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleBuilderBase;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleData;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleThread;
import lombok.Getter;

public class ScheduleBuilderT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    ScheduleBuilderT2(ScheduleData data) {
        this.data = data;
    }

    public ScheduleThread run(Runnable runnable) {
        data.setRunnable(runnable);
        return new ScheduleThread(data);
    }

}
