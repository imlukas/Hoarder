package dev.imlukas.hoarderplugin.utils.command.command.impl;

import dev.imlukas.hoarderplugin.utils.command.language.AbstractObjectiveModel;
import dev.imlukas.hoarderplugin.utils.command.language.data.ObjectiveMetadata;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AdvancedCommand extends AbstractObjectiveModel<AdvancedCommand.AdvancedExecution> {

    private final Map<String, Consumer<CommandSender>> inputValidationMap = new HashMap<>();

    public AdvancedCommand(String syntax) {
        super(syntax);
    }

    public void addInputValidation(String input, Consumer<CommandSender> consumer) {
        inputValidationMap.put(input, consumer);
    }

    public abstract void execute(CommandSender sender, ExecutionContext context);

    @Override
    public final AdvancedExecution compile(ObjectiveMetadata metadata) {
        return new AdvancedExecution(metadata);
    }

    class AdvancedExecution extends ExecutionContext {

        public AdvancedExecution(ObjectiveMetadata metadata) {
            super(metadata);
        }

        @Override
        public void execute(CommandSender sender) {
            for (Map.Entry<String, Consumer<CommandSender>> entry : inputValidationMap.entrySet()) {
                String input = entry.getKey();
                Consumer<CommandSender> consumer = entry.getValue();

                if (getParameter(input) == null) {
                    consumer.accept(sender);
                    return;
                }
            }

            AdvancedCommand.this.execute(sender, this);
        }
    }


}
