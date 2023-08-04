package dev.imlukas.hoarderplugin.utils.command.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.command.command.compat.SimpleCommandWrapper;
import dev.imlukas.hoarderplugin.utils.command.language.AbstractObjectiveModel;
import dev.imlukas.hoarderplugin.utils.command.language.CompiledObjective;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.geometry.Pair;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private final List<AbstractObjectiveModel<?>> commands = new ArrayList<>();
    private final Map<String, BukkitBaseCommand> bukkitCommands = new HashMap<>();

    @Getter
    private final Messages messages;
    private final HoarderPlugin plugin;

    public CommandManager(HoarderPlugin plugin) {
        this.messages = plugin.getMessages();
        this.plugin = plugin;
    }

    public void registerCommand(AbstractObjectiveModel<?> model) {
        commands.add(model);
        attemptCreateCommand(model);
    }

    private void attemptCreateCommand(AbstractObjectiveModel<?> model) {
        String firstWord = model.getSyntax().split(" ")[0];

        if (bukkitCommands.containsKey(firstWord)) {
            return;
        }

        BukkitBaseCommand command = new BukkitBaseCommand(this);
        bukkitCommands.put(firstWord, command);

        CommandUtilities.registerCommand(firstWord, plugin, command);
    }


    public void registerCommand(SimpleCommand legacy) {
        registerCommand(new SimpleCommandWrapper(legacy));
    }

    public List<String> tabComplete(String line) {
        // remove double spaces and all
        line = line.replaceAll(" +", " ");

        List<String> completions = new ArrayList<>();

        for (AbstractObjectiveModel<?> command : commands) {
            completions.addAll(command.getSuggestions(line));
        }

        return completions;
    }

    public Pair<AbstractObjectiveModel<?>, CompiledObjective> parse(String line) {
        for (AbstractObjectiveModel<?> command : commands) {
            CompiledObjective compiled = command.parse(line);

            if (compiled == null) {
                continue;
            }

            return new Pair<>(command, compiled);
        }

        return null;
    }

}
