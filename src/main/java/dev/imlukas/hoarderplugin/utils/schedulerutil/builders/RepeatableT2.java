package dev.imlukas.hoarderplugin.utils.schedulerutil.builders;

import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleBuilderBase;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleData;
import lombok.Getter;

public class RepeatableT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    RepeatableT2(ScheduleData data) {
        this.data = data;
    }

    public RepeatableBuilder run(Runnable runnable) {
        data.setRunnable(runnable);
        return new RepeatableBuilder(data);
    }
}
