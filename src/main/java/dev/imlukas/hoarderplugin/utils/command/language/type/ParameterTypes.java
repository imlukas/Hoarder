package dev.imlukas.hoarderplugin.utils.command.language.type;

import dev.imlukas.hoarderplugin.utils.command.language.type.impl.IntegerParameterType;
import dev.imlukas.hoarderplugin.utils.command.language.type.impl.NumericalParameterType;
import dev.imlukas.hoarderplugin.utils.command.language.type.impl.StringParameterType;
import dev.imlukas.hoarderplugin.utils.command.language.type.impl.filtered.TimeParameterType;
import dev.imlukas.hoarderplugin.utils.command.language.unit.MinecraftTime;

public class ParameterTypes {

    public static final ParameterType<String> STRING = new StringParameterType();
    public static final ParameterType<Integer> INTEGER = new IntegerParameterType();
    public static final ParameterType<Double> NUMERICAL = new NumericalParameterType();
    public static final ParameterType<MinecraftTime> MINECRAFT_TIME = new TimeParameterType();

}
