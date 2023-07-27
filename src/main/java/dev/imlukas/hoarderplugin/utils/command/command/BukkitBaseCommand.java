package dev.imlukas.hoarderplugin.utils.command.command;

import dev.imlukas.hoarderplugin.utils.command.language.AbstractObjectiveModel;
import dev.imlukas.hoarderplugin.utils.command.language.CompiledObjective;
import dev.imlukas.hoarderplugin.utils.geometry.Pair;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitBaseCommand implements CommandExecutor, TabCompleter {

    private final CommandManager manager;
    private final Messages messages;

    protected BukkitBaseCommand(CommandManager manager) {
        this.manager = manager;
        this.messages = manager.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String fullLine = (s + " " + String.join(" ", strings)).trim();

        Pair<AbstractObjectiveModel<?>, CompiledObjective> pair = manager.parse(fullLine);

        if (pair == null) {
            messages.sendMessage(commandSender, "command.invalid");
            return true;
        }

        AbstractObjectiveModel<?> model = pair.getKey();
        CompiledObjective objective = pair.getValue();

        if (!model.canExecute(commandSender)) {
            messages.sendMessage(commandSender, "command.cannot-use");
            return true;
        }

        String permission = model.getPermission();

        if (permission != null && !permission.isEmpty() && !commandSender.hasPermission(permission)) {
            messages.sendMessage(commandSender, "command.no-permission");
            return true;
        }

        objective.execute(commandSender);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // we can't trim as we need to know the last word
        String fullLine = (s + " " + String.join(" ", strings));

        if (strings.length == 0) {
            return manager.tabComplete(s);
        }

        return manager.tabComplete(fullLine);
    }
}
