package dev.imlukas.hoarderplugin.utils.command.language.type.impl;

import dev.imlukas.hoarderplugin.utils.command.language.type.ParameterType;


public class IntegerParameterType implements ParameterType<Integer> {

    @Override
    public boolean isType(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer parse(String input) {
        return Integer.parseInt(input);
    }

    @Override
    public Integer getDefaultValue() {
        return 1;
    }
}
