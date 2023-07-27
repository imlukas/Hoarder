package dev.imlukas.hoarderplugin.utils.command.language.parser.argument;

import dev.imlukas.hoarderplugin.utils.command.language.parser.ArgumentType;
import dev.imlukas.hoarderplugin.utils.command.language.type.ParameterType;

import java.util.List;

public class ParameterArgument<Type> extends Argument<Type> {

    private final ParameterType<?> type;

    public ParameterArgument(String name, ParameterType<?> type, boolean optional, Object value) {
        super(name, value instanceof List<?> ? ArgumentType.LIST : ArgumentType.PARAMETER, optional, value);
        this.type = type;
    }

    public ParameterType<?> getType() {
        return type;
    }

    @Override
    public ParameterArgument<Type> clone() {
        return new ParameterArgument<>(getName(), type, isOptional(), getValue());
    }
}
