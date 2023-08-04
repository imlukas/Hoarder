package dev.imlukas.hoarderplugin.utils.command.language.parser.argument;

import dev.imlukas.hoarderplugin.utils.command.language.parser.ArgumentType;
import lombok.Getter;

public class Argument<T> {

    @Getter
    private final String name;
    private final ArgumentType type;
    @Getter
    private boolean optional;

    @Getter
    private Object value;

    public Argument(String name, ArgumentType type, boolean optional, Object value) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.value = value;
    }

    public ArgumentType getArgumentType() {
        return type;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Argument<T> clone() {
        return new Argument<>(name, type, optional, value);
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", optional=" + optional +
                ", value=" + value +
                '}';
    }
}
