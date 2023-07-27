package dev.imlukas.hoarderplugin.utils.command.command.impl;

import dev.imlukas.hoarderplugin.utils.command.language.CompiledObjective;
import dev.imlukas.hoarderplugin.utils.command.language.data.ObjectiveMetadata;

public abstract class ExecutionContext extends CompiledObjective { // Just a friendly rename

    public ExecutionContext(ObjectiveMetadata metadata) {
        super(metadata);
    }
}
