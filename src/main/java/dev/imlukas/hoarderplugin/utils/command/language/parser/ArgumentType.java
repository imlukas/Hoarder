package dev.imlukas.hoarderplugin.utils.command.language.parser;

public enum ArgumentType {

    PARAMETER,
    TAG,
    LIST,
    STRING;

    public boolean isLiteral() {
        return this == TAG || this == STRING;
    }
}
