package dev.imlukas.hoarderplugin.utils.command.language.data;

import dev.imlukas.hoarderplugin.utils.command.language.parser.argument.Argument;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ObjectiveMetadata {

    private final List<Argument<?>> allArguments;
    private final List<Argument<?>> arguments;
    private final String originalSyntax;

    public ObjectiveMetadata(List<Argument<?>> allArguments, List<Argument<?>> arguments, String originalSyntax) {
        this.allArguments = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.originalSyntax = originalSyntax;

        for (Argument<?> argument : allArguments) {
            this.allArguments.add(argument.clone());
        }

        for (Argument<?> argument : arguments) {
            this.arguments.add(argument.clone());
        }
    }
}
