package dev.imlukas.hoarderplugin.utils.schedulerutil.builders;

import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleBuilderBase;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleData;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleThread;
import dev.imlukas.hoarderplugin.utils.schedulerutil.data.ScheduleTimestamp;
import lombok.Getter;

@Getter
public class RepeatableBuilder extends ScheduleThread implements ScheduleBuilderBase {

    private final ScheduleData data;


    RepeatableBuilder(ScheduleData data) {
        super(data);
        this.data = data;
    }

    public ScheduleTimestamp<ScheduleThread> during(long amount) {
        return new ScheduleTimestamp<>(new ScheduleThread(data), amount, data::setCancelIn);
    }
}
