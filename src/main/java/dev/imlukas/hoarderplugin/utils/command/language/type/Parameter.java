package dev.imlukas.hoarderplugin.utils.command.language.type;

import lombok.Getter;

@Getter
public class Parameter<Type> {

    private final String name;
    private final ParameterType<Type> type;

    private final boolean optional;

    public Parameter(String name, ParameterType<Type> type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public Type parse(String input) {
        return type.parse(input);
    }

}
