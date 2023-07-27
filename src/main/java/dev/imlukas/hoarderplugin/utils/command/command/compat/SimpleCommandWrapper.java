package dev.imlukas.hoarderplugin.utils.command.command.compat;

import dev.imlukas.hoarderplugin.utils.command.language.AbstractObjectiveModel;
import dev.imlukas.hoarderplugin.utils.command.language.CompiledObjective;
import dev.imlukas.hoarderplugin.utils.command.language.data.ObjectiveMetadata;
import dev.imlukas.hoarderplugin.utils.command.language.type.Parameter;
import dev.imlukas.hoarderplugin.utils.command.language.type.ParameterTypes;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import org.bukkit.command.CommandSender;

public class SimpleCommandWrapper extends AbstractObjectiveModel<SimpleCommandWrapper.SimpleCommandExecutor> {

    private final SimpleCommand command;
    private final int paramCount;

    public SimpleCommandWrapper(SimpleCommand command) {
        super(getSyntax(command));

        this.command = command;

        int paramIndex = 1;
        for (String sub : command.getIdentifier().split("\\.")) {
            if (sub.equalsIgnoreCase("*")) {
                registerParameter(new Parameter<>("param" + paramIndex++, ParameterTypes.STRING, true));
            }
        }

        paramCount = paramIndex - 1;
    }

    private static String getSyntax(SimpleCommand command) {
        StringBuilder builder = new StringBuilder();
        int paramIndex = 1;

        String[] split = command.getIdentifier().split("\\.");

        for (String sub : split) {
            if (sub.equalsIgnoreCase("*")) {
                builder.append("<param").append(paramIndex).append("> ");
            } else {
                builder.append(sub).append(" ");
            }
        }

        return builder.toString();
    }

    @Override
    public SimpleCommandExecutor compile(ObjectiveMetadata metadata) {
        return new SimpleCommandExecutor(metadata);
    }

    public class SimpleCommandExecutor extends CompiledObjective {

        public SimpleCommandExecutor(ObjectiveMetadata metadata) {
            super(metadata);
        }

        @Override
        public void execute(CommandSender sender) {
            String[] args = new String[paramCount];

            for (int index = 0; index < paramCount; index++) {
                args[index] = getParameter("param" + (index + 1));
            }

            command.execute(sender, args);
        }
    }
}
